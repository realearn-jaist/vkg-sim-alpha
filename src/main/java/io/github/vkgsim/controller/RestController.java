package io.github.vkgsim.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vkgsim.model.OntopModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
        similarityController.loadAPI_KEY();
        return ontopController.createUserFolder();
    }

    // This method is used to upload the OWL, properties, and driver files
    @PostMapping("/uploadFile")
    public String uploadFile(
            @RequestParam("owlFile") MultipartFile owlFile,
            @RequestParam("mappingFile") MultipartFile mappingFile,
            @RequestParam("propertiesFile") MultipartFile propertiesFile,
            @RequestParam("driverFile") MultipartFile driverFile,
            @RequestParam("api_key") String api_key
    ) {
        try {
            ontopController.initial(owlFile, mappingFile, propertiesFile, driverFile);
            similarityController.setAPI_KEY(api_key);
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

    @DeleteMapping("/deleteProfile")
    public ResponseEntity<String> deleteProfile() {
        ontopController.deleteProfile();
        similarityController.deleteProfile();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // This method is used to send SPAQRL queries to the Ontop CLI
    @PostMapping("/sendQuery")
    public ResponseEntity<String> sendQuery(
            @RequestBody Map<String, String> request
    ) {
        String sparqlQuery = request.get("query");
        String owlFileType = request.get("owlFileType");
        String queryType = request.get("queryType");
        try {
            return new ResponseEntity<>(ontopController.ontopQuery(String.valueOf(sparqlQuery) , owlFileType, queryType), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error executing SPARQL query: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This method is used to generate the mapping file
    @GetMapping("/generateMapping")
    public ResponseEntity<String> generateMapping(@RequestParam String baseIRI) {
        try {
            return new ResponseEntity<>(ontopController.ontopBootstrap(baseIRI), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
    public ResponseEntity<String> readMappingFileContent() {
        try {
            return new ResponseEntity<>(ontopController.readMappingFileContent(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    // This method is used to read the similarity file content
    @GetMapping("/readSimilarityFileContent")
    public ResponseEntity<List<Map<String, Object>>> readSimilarityFileContent() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            JSONArray jsonArray = ontopController.readSimilarityFileContent();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                result.add(jsonObject.toMap());
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            // Return an error response with a 500 Internal Server Error status
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
    public ResponseEntity<HttpStatus> saveSimResultFile(
            @RequestBody Map<String, String> request
    ) {
        String result = request.get("result");
        if (!result.isEmpty()) {
            ontopController.saveSimResultFile(result);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

}