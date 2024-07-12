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

    private final String mappingFileName = "mapping.obda"; // Name of the mapping file
    private String owlFileName; // Name of the OWL file
    private String propertiesFileName; // Name of the properties file
    private String driverFileName; // Name of the driver file

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

    public File saveUploadedFile(MultipartFile file, String filename) throws IOException {
        /*
          Save the uploaded file to the server
          @param file: MultipartFile object
          @param filename: Name of the file
         * @return savedFile: File object
         */
        String USER_DIR = "ontop-cli/" + BASE_UPLOAD_DIR + username + "/";
        File uploadDir = new File(USER_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        System.out.println("File saved to: " + uploadDir.getAbsolutePath());
        File savedFile = new File(uploadDir, filename);
        file.transferTo(new File(savedFile.getAbsolutePath()));
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

    private String readMappingFileContent() {
        /*
          Read the content of the mapping file
         * @return contentBuilder.toString(): Content of the mapping file
         */
        try {
            String TMP_MappingFileName = "ontop-cli/" + getBaseUploadDir() + getUsername() + "/" + mappingFileName;
            File file = new File(TMP_MappingFileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            reader.close();
            return contentBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading mapping file.";
        }
    }

    public String createUserFolder(String username) {
        /*
          Create a folder for the user
          @param username: Username of the user
         * @return userFolder.toString(): Path to the user folder
         */
        Path userFolder = Paths.get(System.getProperty("user.dir"), "ontop-cli", "inputFiles", username);
        try {
            Files.createDirectories(userFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error creating user folder.";
        }
        return userFolder.toString();
    }

    public String getOwlFileName() {
        return owlFileName;
    }

    public void setOwlFileName(String owlFileName) {
        this.owlFileName = owlFileName;
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

    public String executeCommand(String command) {
        /*
          Execute a command in the command prompt
          @param command: Command to be executed
         * @return output.toString(): Output of the command
         */
        StringBuilder output = new StringBuilder();
        try {
            // Change directory and execute command
            command = "cd ontop-cli && " + command;

            // Create ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c", command); // For Windows

            // Start the process
            Process process = processBuilder.start();

            // Get input and error streams
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Read output
            String line;
            while ((line = reader.readLine()) != null) {
                output.append("Output: ").append(line).append("\n");
            }

//            // Read errors
//            while ((line = errorReader.readLine()) != null) {
//                output.append("Error: ").append(line).append("\n");
//            }

//            // Wait for the process to finish
//            int exitCode = process.waitFor();
//            output.append("Exited with error code: ").append(exitCode);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(output);
        return output.toString();
    }

    public String ontopQuery(String sparqlQuery) {
        /*
          Execute a SPARQL query using Ontop
          @param sparqlQuery: SPARQL query to be executed
         * @return result: Result of the query
         */
        File tempQueryFile;
        try {
            // Specify the ontop-cli directory within the current working directory
            Path ontopCliDir = Paths.get(System.getProperty("user.dir"), "ontop-cli");
            // Create the temporary file in the ontop-cli directory
            tempQueryFile = File.createTempFile("sparqlQuery", ".txt", ontopCliDir.toFile());
            try (FileWriter writer = new FileWriter(tempQueryFile)) {
                writer.write(sparqlQuery);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error creating temporary SPARQL query file.";
        }
        System.out.println(sparqlQuery);
        System.out.println(tempQueryFile.getAbsolutePath());

        String TMP_MappingFileName = getBaseUploadDir() + getUsername() + "/" + mappingFileName;
        String TMP_OWLFileName = getBaseUploadDir() + getUsername() + "/" + owlFileName;
        String TMP_PropertiesFileName = getBaseUploadDir() + getUsername() + "/" + propertiesFileName;

        String command = "ontop query -m " + TMP_MappingFileName +
                " -t " + TMP_OWLFileName +
                " -p " + TMP_PropertiesFileName +
                " -q " + tempQueryFile.getName();

        String result = executeCommand(command);

        // Delete the temporary SPARQL query file
        if (tempQueryFile != null && tempQueryFile.exists()) {
            tempQueryFile.delete();
        }
        return result;
    }

    public String ontopBootstrap(String baseIRI) {

        String TMP_MappingFileName = getBaseUploadDir() + getUsername() + "/" + mappingFileName;
        String TMP_OWLFileName = getBaseUploadDir() + getUsername() + "/" + owlFileName + "_tmp";
        String TMP_PropertiesFileName = getBaseUploadDir() + getUsername() + "/" + propertiesFileName;
        String command = "ontop bootstrap --base-iri " + baseIRI + " -m " + TMP_MappingFileName +
                " -t " + TMP_OWLFileName +
                " -p " + TMP_PropertiesFileName;

        System.out.println(command);
        executeCommand(command);
        return readMappingFileContent();
    }


}
