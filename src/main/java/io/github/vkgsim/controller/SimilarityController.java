package io.github.vkgsim.controller;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import sim.explainer.library.SimExplainer;
import sim.explainer.library.enumeration.ImplementationMethod;

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
     * 
     * @return
     */
    public String readAllConceptWithThreshold() {
        try {
            String inputFile = ontopController.buildFilePath("");
            SimExplainer simExplainer = new SimExplainer(inputFile);
            List<String> conceptNames = simExplainer.retrieveConceptName();
            System.out.println(" Concept Names: " + conceptNames);
            simExplainer.setApiKey("YOUR API KEY");
            StringBuilder resultBuilder = new StringBuilder();
            for (int i = 0; i < conceptNames.size(); i++) {
                for (int j = i + 1; j < conceptNames.size(); j++) {
                    String concept1 = conceptNames.get(i);
                    String concept2 = conceptNames.get(j);
                    String similarity = simExplainer.similarity(ImplementationMethod.DYNAMIC_SIM, concept1, concept2)
                            .toString();
                    JSONObject explanationNL = simExplainer.getExplantionAsNaturalLanguage(concept1, concept2);
                    String forwardExplanation = explanationNL.getJSONObject("forward").getString("explanationMessage");
                    String backwardExplanation = explanationNL.getJSONObject("backward")
                            .getString("explanationMessage");
                    String sumExplanation = explanationNL.getString("explanation");
                    System.out.println(concept1 + " and " + concept2 + " similarity: " + similarity + " Explanation: "
                            + sumExplanation);
                    JSONObject descTree1 = simExplainer.treeHierachyAsJson(concept1);
                    JSONObject descTree2 = simExplainer.treeHierachyAsJson(concept2);
                    System.out.println("Desc Tree 1: " + descTree1);

                    resultBuilder.append(concept1).append("|")
                            .append(concept2).append("|")
                            .append(similarity).append("|")
                            .append(forwardExplanation).append("|")
                            .append(backwardExplanation).append("|")
                            .append(sumExplanation).append("|")
                            .append(descTree1.toString()).append("|")
                            .append(descTree2.toString()).append("\n");
                }
            }

            // save result to file
            String outputFile = ontopController.buildFilePath("similarity.txt");
            saveSimilarityResultFile(outputFile, resultBuilder.toString());
            return resultBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * This method is used to save the similarity result to a file
     * 
     * @param outputFile
     * @param content
     */
    void saveSimilarityResultFile(String outputFile, String content) {
        try {
            Path outputPath = Paths.get(outputFile);
            // Ensure the parent directories exist
            Files.createDirectories(outputPath.getParent());
            // Write the content to the file
            Files.write(outputPath, content.getBytes());
            System.out.println("File saved: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save file: " + outputFile);
        }
    }

}
