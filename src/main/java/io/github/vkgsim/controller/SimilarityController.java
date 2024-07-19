package io.github.vkgsim.controller;

import org.springframework.stereotype.Component;
import sim.explainer.library.SimExplainer;
import sim.explainer.library.enumeration.TypeConstant;

import java.util.List;

@Component
public class SimilarityController {

    private final OntopController ontopController; // Dependency Injection

    // Constructor
    public SimilarityController(OntopController ontopController) {
        this.ontopController = ontopController;
    }

    public String fetchAllThresholdConceptPair(String threshold) {
        /*
            * Fetch all threshold concept pair from the KRSS file
            * @param threshold: Threshold value
            * @return: String
         */
        System.out.println("Threshold: " + threshold);
        String inputKRSSFile = ontopController.buildFilePath("");
        System.out.println("Input KRSS File: " + inputKRSSFile);
        String preferenceProfileDir = "./input/preference-profile";
        SimExplainer simExplainerKRSS = new SimExplainer(inputKRSSFile, preferenceProfileDir);
        System.out.println(simExplainerKRSS);

        List<String> conceptNamesKRSS = simExplainerKRSS.retrieveConceptName();
        System.out.println("KRSS Concept Names: " + conceptNamesKRSS);

        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < conceptNamesKRSS.size(); i++) {
            for (int j = i + 1; j < conceptNamesKRSS.size(); j++) {
                String concept1 = conceptNamesKRSS.get(i);
                String concept2 = conceptNamesKRSS.get(j);
                String result_krss = simExplainerKRSS.similarity(TypeConstant.DYNAMIC_SIMPI, concept1, concept2).toString();
                System.out.println("Similarity between " + concept1 + " and " + concept2 + ": " + result_krss);
                boolean b = Float.parseFloat(result_krss) > Float.parseFloat(threshold);
                if (b) {
                    resultBuilder.append("Similarity between ").append(concept1).append(" and ").append(concept2).append(": ").append(result_krss).append("\n");
                }
            }
        }

        return resultBuilder.toString();
    }

}
