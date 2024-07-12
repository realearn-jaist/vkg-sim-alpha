package io.github.vkgsim.controller;

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

    // This method is used to create a folder for the user
    @GetMapping("/createUserFolder")
    public String createUserFolder(@RequestParam String username) {
        ontopController.setUsername(username);
        return ontopController.createUserFolder(username);
    }

    // This method is used to upload the OWL, properties, and driver files
    @PostMapping("/uploadFile")
    public String uploadFile(
            @RequestParam("owlFile") MultipartFile owlFile,
            @RequestParam("propertiesFile") MultipartFile propertiesFile,
            @RequestParam("driverFile") MultipartFile driverFile
    ) {
        ontopController.setOwlFileName(owlFile.getOriginalFilename());
        ontopController.setPropertiesFileName(propertiesFile.getOriginalFilename());
        ontopController.setDriverFileName(driverFile.getOriginalFilename());
        try {
            File owl = ontopController.saveUploadedFile(owlFile, owlFile.getOriginalFilename());
            File properties = ontopController.saveUploadedFile(propertiesFile, propertiesFile.getOriginalFilename());
            File driver = ontopController.saveUploadedFile(driverFile, driverFile.getOriginalFilename());

        } catch (IOException e) {
            e.printStackTrace();
            return "Error saving uploaded files.";
        }
        return "Files uploaded successfully.";
    }

    // This method is used to retrieve the concept names from the OWL file
    @GetMapping("/findAllConceptNames")
    public String findAllConceptNames() {
        String owlFileName = "ontop-cli/" + ontopController.getBaseUploadDir() + ontopController.getUsername() + "/" + ontopController.getOwlFileName();
        return ontopController.retrieveConceptName(new File(owlFileName)).toString();
    }

    // This method is used to send SPAQRL queries to the Ontop CLI
    @PostMapping("/sendQuery")
    public String sendQuery(
            @RequestBody Map<String, String> request
    ) {
        String sparqlQuery = request.get("query");
        return ontopController.ontopQuery(String.valueOf(sparqlQuery));
    }

    // This method is used to generate the mapping file
    @GetMapping("/generateMapping")
    public String generateMapping(@RequestParam String baseIRI) {
        return ontopController.ontopBootstrap(baseIRI);
    }

}
