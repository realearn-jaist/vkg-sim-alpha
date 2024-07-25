package io.github.vkgsim.controller;

import io.github.vkgsim.model.OntopModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.io.File;
import java.io.IOException;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private OntopController ontopController;

    @Autowired
    private SimilarityController similarityController;

    // This method is used to create a folder for the user
    @GetMapping("/createUserFolder")
    public String createUserFolder(@RequestParam String username) throws IOException {
        ontopController.setUsername(username);
        return ontopController.createUserFolder();
    }

    // This method is used to upload the OWL, properties, and driver files
    @PostMapping("/uploadFile")
    public String uploadFile(
            @RequestParam("owlFile") MultipartFile owlFile,
            @RequestParam("mappingFile") MultipartFile mappingFile,
            @RequestParam("propertiesFile") MultipartFile propertiesFile,
            @RequestParam("driverFile") MultipartFile driverFile
    ) {
        try {
            ontopController.initial(owlFile, mappingFile, propertiesFile, driverFile);

        } catch (IOException e) {
            e.printStackTrace();
            return "Error saving uploaded files.";
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
        return "Files uploaded successfully.";
    }

    // This method is used to retrieve the concept names from the OWL file
    @GetMapping("/findAllConceptNames")
    public List<String> findAllConceptNames() {
        return ontopController.retrieveConceptName();
    }

    // This method is used to send SPAQRL queries to the Ontop CLI
    @PostMapping("/sendQuery")
    public String sendQuery(
            @RequestBody Map<String, String> request
    ) {
        String sparqlQuery = request.get("query");
        String owlFileType = request.get("owlFileType");
        String queryType = request.get("queryType");
        return ontopController.ontopQuery(String.valueOf(sparqlQuery) , owlFileType, queryType);
    }

    // This method is used to generate the mapping file
    @GetMapping("/generateMapping")
    public String generateMapping(@RequestParam String baseIRI) {
        return ontopController.ontopBootstrap(baseIRI);
    }

    // This method is used to save the mapping file
    @PostMapping("/saveMapping")
    public String saveMapping(
            @RequestBody Map<String, String> request
    ) {
        String mapping = request.get("mapping");
        return ontopController.saveMapping(mapping);
    }

    // This method is used to read the mapping file content
    @GetMapping("/readMappingFileContent")
    public String readMappingFileContent() {
        return ontopController.readMappingFileContent();
    }

    // This method is used to read the similarity file content
    @GetMapping("/readSimilarityFileContent")
    public List<Map<String, Object>> readSimilarityFileContent() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            JSONArray jsonArray = ontopController.readSimilarityFileContent();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                result.add(jsonObject.toMap()); // Convert JSONObject to Map<String, Object>
            }
        } catch (Exception e) {
            // Log the exception and return an empty list or handle the error as needed
            // logger.error("Error reading similarity file content", e);
        }
        return result;
    }

    // This method is used to read the concept name file content
    @GetMapping("/readConceptNameFile")
    public String readConceptNameFile() {
        return ontopController.readConceptNameFile();
    }

    // This method is used to measure the similarity in all concept pairs
    @GetMapping("/similarityMeasureAllConcept")
    public String similarityMeasureAllConcept() {
        return similarityController.readAllConceptWithThreshold();
    }

    // This method is used to retrieve the owl file name (normal and bootstrap)
    @GetMapping("/getOWLFilename")
    public List<String> getOWLFilename() {
        return ontopController.getOWLFileNameWithBoostrap();
    }

    @PostMapping("/saveSimResultFile")
    public void saveSimResultFile(
            @RequestBody Map<String, String> request
    ) {
        String result = request.get("result");
        ontopController.saveSimResultFile(result);
    }

}