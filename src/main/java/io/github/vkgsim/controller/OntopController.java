package io.github.vkgsim.controller;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class OntopController {

    private static final String BASE_UPLOAD_DIR = "inputFiles/"; // Directory to store uploaded files
    private final Set<String> processedProperties = new HashSet<>(); // Set to store processed properties

    private String username; // Username of the user
    private String mappingFileName; // Name of the mapping file
    private String owlFileName; // Name of the OWL file
    private String propertiesFileName; // Name of the properties file
    private String driverFileName; // Name of the driver file

    ///////////////////////////
    ////Setters and Getters////
    ///////////////////////////
    public String getOwlFileName() {
        return owlFileName;
    }

    public void setOwlFileName(String owlFileName) {
        this.owlFileName = owlFileName;
    }

    public ArrayList<String> getOWLFileNameWithBoostrap() {
        //String baseOWLFilename = buildFilePath("");
        ArrayList<String> fileNames = new ArrayList<>();
        addFileNameIfExists(fileNames, this.owlFileName);
        addFileNameIfExists(fileNames, getBootstrapFileName());
        return fileNames;
    }

    private String getBootstrapFileName() {
        return this.owlFileName.substring(0, this.owlFileName.lastIndexOf('.')) + "_tmp.owl";
    }

    private void addFileNameIfExists(List<String> fileNames, String fileName) {
        Path filePath = Paths.get(buildFilePath(fileName));
        if (Files.exists(filePath)) {
            fileNames.add(filePath.getFileName().toString());
        }
    }

    public String buildFilePath(String fileName) {
        return "ontop-cli/" + BASE_UPLOAD_DIR + getUsername() + "/" + fileName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPropertiesFileName() {
        return propertiesFileName;
    }

    public void setPropertiesFileName(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
    }

    public String getDriverFileName() {
        return driverFileName;
    }

    public void setDriverFileName(String driverFileName) {
        this.driverFileName = driverFileName;
    }

    public String getBaseUploadDir() {
        return BASE_UPLOAD_DIR;
    }

    public String getMappingFileName() {
        return mappingFileName;
    }

    public void setMappingFileName(String mappingFileName) {
        this.mappingFileName = mappingFileName;
    }

    ///////////////////////////
    ///////Ontop Methods//////
    ///////////////////////////
    public OWLOntology prepareOWLFile(File owlFile) {
        /*
          Prepare the OWL file for processing
          @param owlFile: OWL file to be processed
         * @return ontology: OWLOntology object
         */
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(IRI.create(owlFile));
        } catch (OWLOntologyCreationException e) {
            System.err.println("Failed to load ontology: " + e.getMessage());
            e.printStackTrace();
        }
        return ontology;
    }

    public String saveMapping(String mapping) {
        String TMP_MappingFileName = buildFilePath(mappingFileName);
        try (FileWriter writer = new FileWriter(TMP_MappingFileName)) {
            writer.write(mapping);
        } catch (IOException e) {
            System.err.println("Failed to save mapping: " + e.getMessage());
        }
        return mapping;
    }

    public File saveUploadedFile(MultipartFile file, String filename) throws IOException {
        Path uploadDirPath = Paths.get("ontop-cli", BASE_UPLOAD_DIR, username);
        Files.createDirectories(uploadDirPath);
        File savedFile = new File(uploadDirPath.toFile(), filename);
        file.transferTo(savedFile.toPath());
        System.out.println("File saved: " + savedFile.getAbsolutePath());
        return savedFile;
    }

    public void addObjectProperties(OWLOntology ontology, OWLClass owlClass, ShortFormProvider shortFormProvider) {
        /*
          Retrieve object properties for a given OWL class
          @param ontology: OWLOntology object
          @param owlClass: OWLClass object
          @param shortFormProvider: ShortFormProvider object
         */
        for (OWLIndividual individual : owlClass.getIndividuals(ontology)) {
            for (OWLObjectPropertyAssertionAxiom axiom : ontology.getObjectPropertyAssertionAxioms(individual)) {
                OWLObjectProperty property = axiom.getProperty().asOWLObjectProperty();

                String propertyName = shortFormProvider.getShortForm(property);

                processedProperties.add(propertyName);
            }
        }
    }

    public List<String> retrieveConceptName(File owlFile) {
        /*
          Retrieve concept names from the OWL file
          @param owlFile: OWL file
         * @return conceptNames: List of concept names
         */
        List<String> conceptNames = new ArrayList<>();
        OWLOntology ontology = prepareOWLFile(owlFile);
        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();

        for (OWLClass owlClass1 : ontology.getClassesInSignature()) {
            String className1 = shortFormProvider.getShortForm(owlClass1);

            addObjectProperties(ontology, owlClass1, shortFormProvider);

            if (!className1.equals("Thing")) {
                conceptNames.add(className1);
            }
        }
        return conceptNames;
    }

    public String readMappingFileContent() {
        String TMP_MappingFileName = buildFilePath(mappingFileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(TMP_MappingFileName))) {
            StringBuilder contentBuilder = new StringBuilder();
            reader.lines().forEach(line -> contentBuilder.append(line).append("\n"));
            return contentBuilder.toString();
        } catch (IOException e) {
            System.err.println("Error reading mapping file: " + e.getMessage());
            return "Error reading mapping file.";
        }
    }

    public String createUserFolder(String username) {
        Path userFolder = Paths.get("ontop-cli", "inputFiles", username);
        try {
            Files.createDirectories(userFolder);
            return userFolder.toString();
        } catch (IOException e) {
            System.err.println("Error creating user folder: " + e.getMessage());
            return "Error creating user folder.";
        }
    }


    public String executeCommand(String command) {
        System.out.println("Executing command: " + command);
        StringBuilder output = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "cd ontop-cli && " + command);
        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                reader.lines().forEach(line -> output.append("Output: ").append(line).append("\n"));
                errorReader.lines().forEach(line -> output.append("Error: ").append(line).append("\n"));
                System.out.println("Output: " + output.toString());
            }
            int exitCode = process.waitFor();
            output.append("Exited with error code: ").append(exitCode).append("\n");
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Command execution failed: " + e.getMessage());
        }
        return output.toString();
    }

    private File createTempFile(Path directory, String content, String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix, directory.toFile());
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }
        System.out.println("Created temporary file: " + tempFile.getAbsolutePath());
        return tempFile;
    }

    private String buildOntopCommand(String action, String queryFileName, String owlFileName) {
        String TMP_MappingFileName = getBaseUploadDir() + getUsername() + "/" + mappingFileName;
        String TMP_OWLFileName = getBaseUploadDir() + getUsername() + "/" + owlFileName;
        String TMP_PropertiesFileName = getBaseUploadDir() + getUsername() + "/" + propertiesFileName;

        return String.format("ontop %s -m %s -t %s -p %s %s", action, TMP_MappingFileName, TMP_OWLFileName, TMP_PropertiesFileName,
                queryFileName != null ? "-q " + queryFileName : "");
    }

    private void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    public String ontopQuery(String sparqlQuery, String owlFileType) {
        Path ontopCliDir = Paths.get(System.getProperty("user.dir"), "ontop-cli");
        try {
            // Create a temporary SPARQL query file
            File tempQueryFile = createTempFile(ontopCliDir, sparqlQuery, "sparqlQuery", ".txt");

            // Build command with temporary file
            String command = buildOntopCommand("query", tempQueryFile.getName(), owlFileType);

            // Execute command and handle result
            try {
                return executeCommand(command);
            } finally {
                // Ensure the temporary file is deleted
                deleteTempFile(tempQueryFile);
            }
        } catch (IOException e) {
            return "Error executing SPARQL query: " + e.getMessage();
        }
    }

    public String ontopBootstrap(String baseIRI) {
        // Update the owlFileName to include "_tmp" before the extension
        String owlFileName = this.owlFileName.substring(0, this.owlFileName.lastIndexOf('.')) + "_tmp.owl"; // Apply the temporary file name change

        // Build the command for bootstrapping
        String command = buildOntopCommand("bootstrap --base-iri " + baseIRI, null, owlFileName);

        // Execute the command
        executeCommand(command);

        // Return the content of the mapping file
        return readMappingFileContent();
    }


}
