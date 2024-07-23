package io.github.vkgsim.controller;

import org.springframework.stereotype.Component;
import sim.explainer.library.SimExplainer;
import sim.explainer.library.enumeration.TypeConstant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class SimilarityController {

    private final OntopController ontopController; // Dependency Injection

    // Constructor
    public SimilarityController(OntopController ontopController) {
        this.ontopController = ontopController;
    }

    /**
     * This method is used to fetch all threshold concept pair
     * @return
     */
    public String readAllConceptWithThreshold() {
        try{
        String inputKRSSFile = ontopController.buildFilePath("");
        //System.out.println("Input KRSS File: " + inputKRSSFile);
        //String preferenceProfileDir = "./input/preference-profile";
        SimExplainer simExplainerKRSS = new SimExplainer(inputKRSSFile);
        //System.out.println(simExplainerKRSS);

        List<String> conceptNamesKRSS = simExplainerKRSS.retrieveConceptName();
        System.out.println("KRSS Concept Names: " + conceptNamesKRSS);

        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < conceptNamesKRSS.size(); i++) {
            for (int j = i + 1; j < conceptNamesKRSS.size(); j++) {
                String concept1 = conceptNamesKRSS.get(i);
                String concept2 = conceptNamesKRSS.get(j);
                String result_krss = simExplainerKRSS.similarity(TypeConstant.DYNAMIC_SIMPI, concept1, concept2).toString();
                System.out.println("Similarity between " + concept1 + " and " + concept2 + ": " + result_krss);
                resultBuilder.append(concept1).append(",").append(concept2).append(",").append(result_krss).append("\n");
            }
        }

        //save result to file
        String outputKRSSFile = ontopController.buildFilePath("similarity.txt");
        saveSimilarityResultFile(outputKRSSFile, resultBuilder.toString());
        return resultBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * This method is used to save the similarity result to a file
     * @param outputKRSSFile
     * @param content
     */
    void saveSimilarityResultFile(String outputKRSSFile, String content) {
        try {
            Path outputPath = Paths.get(outputKRSSFile);
            // Ensure the parent directories exist
            Files.createDirectories(outputPath.getParent());
            // Write the content to the file
            Files.write(outputPath, content.getBytes());
            System.out.println("File saved: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save file: " + outputKRSSFile);
        }
    }

}
