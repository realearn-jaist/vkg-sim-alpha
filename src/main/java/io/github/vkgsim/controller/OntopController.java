package io.github.vkgsim.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.vkgsim.util.SymmetricPair;
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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.StandardOpenOption;

@Component
public class OntopController {

    private static String OntopDir = "ontop-cli/";
    private static String BASE_UPLOAD_DIR = "inputFiles/"; // Directory to store uploaded files
    final Set<String> processedProperties = new HashSet<>(); // Set to store processed properties

    private String username; // Username of the user
    private String mappingFileName; // Name of the mapping file
    private String owlFileName; // Name of the OWL file
    private String propertiesFileName; // Name of the properties file
    private String driverFileName; // Name of the driver file
    private String simFileName = "similarity.txt"; // Name of the similarity file
    private String conceptFileName = "conceptNames.txt"; // Name of the concept file
    private String baseIRI = "http://";
    private ArrayList<SymmetricPair<String>> rewritingConcept = new ArrayList<>();
    private ProcessBuilder processBuilder; // Process builder to execute commands

    ///////////////////////////
    ////Setters and Getters////
    ///////////////////////////
    // Getter and Setter for owlFileName
    public String getOwlFileName() {
        return owlFileName;
    }

    public void setOwlFileName(String owlFileName) {
        this.owlFileName = owlFileName;
    }

    // Getter and Setter for simFileName
    public String getSimFileName() {
        return simFileName;
    }

    public void setSimFileName(String simFileName) {
        this.simFileName = simFileName;
    }

    // Getter and Setter for conceptFileName
    public String getConceptFileName() {
        return conceptFileName;
    }

    public void setConceptFileName(String conceptFileName) {
        this.conceptFileName = conceptFileName;
    }

    // Getter and Setter for propertiesFileName
    public String getPropertiesFileName() {
        return propertiesFileName;
    }

    public void setPropertiesFileName(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
    }

    // Getter and Setter for driverFileName
    public String getDriverFileName() {
        return driverFileName;
    }

    public void setDriverFileName(String driverFileName) {
        this.driverFileName = driverFileName;
    }

    // Getter and Setter for mappingFileName
    public String getMappingFileName() {
        return mappingFileName;
    }

    public void setMappingFileName(String mappingFileName) {
        this.mappingFileName = mappingFileName;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for OntopDir
    public String getOntopDir() {
        return OntopDir;
    }

    public void setOntopDir(String ontopDir) {
        OntopDir = ontopDir;
    }

    // Getter and Setter for BASE_UPLOAD_DIR
    public String getBaseUploadDir() {
        return BASE_UPLOAD_DIR;
    }

    public void setBaseUploadDir(String baseUploadDir) {
        BASE_UPLOAD_DIR = baseUploadDir;
    }

    // Utility Methods
    public String buildFilePath(String fileName) {
        return OntopDir + BASE_UPLOAD_DIR + getUsername() + "/" + fileName;
    }

    public String getBootstrapFileName() {
        return this.owlFileName.substring(0, this.owlFileName.lastIndexOf('.')) + "_tmp.owl";
    }

    public void setProcessBuilder(ProcessBuilder mockProcessBuilder) {
        this.processBuilder = mockProcessBuilder;
    }

    private void addFileNameIfExists(List<String> fileNames, String fileName) {
        Path filePath = Paths.get(buildFilePath(fileName));
        if (Files.exists(filePath)) {
            fileNames.add(filePath.getFileName().toString());
        }
    }

    public ArrayList<String> getOWLFileNameWithBoostrap() {
        ArrayList<String> fileNames = new ArrayList<>();
        addFileNameIfExists(fileNames, this.owlFileName);
        addFileNameIfExists(fileNames, getBootstrapFileName());
        return fileNames;
    }

    ///////////////////////////
    ///////Ontop Methods//////
    ///////////////////////////

    /**
     * Prepare the OWL file
     * @param owlFile
     * @return
     */
    public OWLOntology prepareOWLFile(File owlFile) {
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

    /**
     * Save the mapping file
     * @param mapping
     * @return
     */
    public String saveMapping(String mapping) {
        String TMP_MappingFileName = buildFilePath(mappingFileName);
        try (FileWriter writer = new FileWriter(TMP_MappingFileName)) {
            writer.write(mapping);
        } catch (IOException e) {
            System.err.println("Failed to save mapping: " + e.getMessage());
            return "Error saving mapping file.";
        }
        return mapping;
    }

    /**
     * Save the uploaded file
     * @param file
     * @param filename
     * @return
     * @throws IOException
     */
    public File saveUploadedFile(MultipartFile file, String filename) throws IOException {
        Path uploadDirPath = Paths.get(OntopDir, BASE_UPLOAD_DIR, username);
        Files.createDirectories(uploadDirPath);
        File savedFile = new File(uploadDirPath.toFile(), filename);
        file.transferTo(savedFile.toPath());
        System.out.println("File saved: " + savedFile.getAbsolutePath());
        return savedFile;
    }

    /**
     * Save the uploaded file with a specific folder that is not the user folder
     * @param file
     * @param filename
     * @param type
     * @return
     * @throws IOException
     */
    public File saveUploadedFile(MultipartFile file, String filename, String type) throws IOException {
        Path uploadDirPath = Paths.get(OntopDir, "jdbc");
        Files.createDirectories(uploadDirPath);
        File savedFile = new File(uploadDirPath.toFile(), filename);
        file.transferTo(savedFile.toPath());
        System.out.println("File saved: " + savedFile.getAbsolutePath());
        return savedFile;
    }


    /**
     * Retrieve object properties for a given OWL class
     * @param ontology
     * @param owlClass
     * @param shortFormProvider
     */
    public void addObjectProperties(OWLOntology ontology, OWLClass owlClass, ShortFormProvider shortFormProvider) {
        for (OWLIndividual individual : owlClass.getIndividuals(ontology)) {
            for (OWLObjectPropertyAssertionAxiom axiom : ontology.getObjectPropertyAssertionAxioms(individual)) {
                OWLObjectProperty property = axiom.getProperty().asOWLObjectProperty();

                String propertyName = shortFormProvider.getShortForm(property);

                processedProperties.add(propertyName);
            }
        }
    }

    /**
     * Retrieve concept names from the OWL file
     * @param owlFile
     * @return
     */
    public List<String> retrieveConceptName(File owlFile) {
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
        // save all concept names to a file
        String TMP_ConceptFileName = buildFilePath(conceptFileName);
        try (FileWriter writer = new FileWriter(TMP_ConceptFileName)) {
            for (String conceptName : conceptNames) {
                writer.write(conceptName + "\n");
            }
        } catch (IOException e) {
            System.err.println("Failed to save concept names: " + e.getMessage());
        }
        System.out.println("Concept names saved to file: " + TMP_ConceptFileName);
        return conceptNames;
    }

    /**
     * Read the content of similarity file
     * @return
     */
    public String readSimilarityFileContent() {
        String TMP_MappingFileName = buildFilePath(simFileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(TMP_MappingFileName))) {
            StringBuilder contentBuilder = new StringBuilder();
            reader.lines().forEach(line -> contentBuilder.append(line).append("\n"));
            return contentBuilder.toString();
        } catch (IOException e) {
            System.err.println("Error reading mapping file: " + e.getMessage());
            return "Error reading mapping file.";
        }
    }

    public void saveSimResultFile(String result) {
        rewritingConcept.clear();
        String[] lines = result.split("\n");
        for (String line : lines) {
            // Split the line into parts
            String[] parts = line.split(", ");

            // Extract Concept 1, Concept 2
            String concept1 = parts[0].split(": ")[1];
            String concept2 = parts[1].split(": ")[1];

            // Add to HashMap
            SymmetricPair<String> simConcept = new SymmetricPair<>(concept1, concept2);
            rewritingConcept.add(simConcept);

        }
    }

    /**
     * Read the content of the mapping file
     * @return
     */
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

    /**
     * Create a user folder
     * @param username
     * @return
     */
    public String createUserFolder(String username) {
        Path userFolder = Paths.get(OntopDir, "inputFiles", username);
        try {
            Files.createDirectories(userFolder);
            return userFolder.toString();
        } catch (IOException e) {
            System.err.println("Error creating user folder: " + e.getMessage());
            return "Error creating user folder.";
        }
    }

    /**
     * Execute a command
     * @param command
     * @return
     */
    public String executeCommand(String command) {
        System.out.println("Executing command: " + command);
        StringBuilder output = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "cd ontop-cli && " + command);
        try {
            System.out.println("Starting command execution...");
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                System.out.println("Command executed successfully.");
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

    /**
     * Create a temporary file
     * @param directory
     * @param content
     * @param prefix
     * @param suffix
     * @return
     * @throws IOException
     */
    public File createTempFile(Path directory, String content, String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix, directory.toFile());
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }
        System.out.println("Created temporary file: " + tempFile.getAbsolutePath());
        return tempFile;
    }

    /**
     * Build the Ontop command
     * @param action
     * @param queryFileName
     * @param owlFileName
     * @return
     */
    private String buildOntopCommand(String action, String queryFileName, String owlFileName, String queryType) {
        String TMP_MappingFileName = getBaseUploadDir() + getUsername() + "/" + mappingFileName;
        if (queryType.equals("similarity")) {
            System.out.println("Similarity search");
            TMP_MappingFileName = TMP_MappingFileName.replace(".obda","_sim.obda");
        }
        String TMP_OWLFileName = getBaseUploadDir() + getUsername() + "/" + owlFileName;
        String TMP_PropertiesFileName = getBaseUploadDir() + getUsername() + "/" + propertiesFileName;

        return String.format("ontop %s -m %s -t %s -p %s %s", action, TMP_MappingFileName, TMP_OWLFileName, TMP_PropertiesFileName,
                queryFileName != null ? "-q " + queryFileName : "");
    }

    /**
     * Delete a temporary file
     * @param file
     */
    public void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * Execute a SPARQL query
     * @param sparqlQuery
     * @param owlFileType
     * @return
     */
    public String ontopQuery(String sparqlQuery, String owlFileType, String queryType) {
        Path ontopCliDir = Paths.get(System.getProperty("user.dir"), OntopDir);
        try {
            // Create a temporary SPARQL query file
            File tempQueryFile = createTempFile(ontopCliDir, sparqlQuery, "sparqlQuery", ".txt");

            if(queryType.equals("similarity")) {
                genMappingSimFile();
            }
            // Build command with temporary file
            String command = buildOntopCommand("query", tempQueryFile.getName(), owlFileType, queryType);

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

    /**
     * Bootstrap the Ontop CLI
     * @param baseIRI
     * @return
     */
    public String ontopBootstrap(String baseIRI) {
        // Update the owlFileName to include "_tmp" before the extension
        String owlFileName = this.owlFileName.substring(0, this.owlFileName.lastIndexOf('.')) + "_tmp.owl"; // Apply the temporary file name change

        // Build the command for bootstrapping
        String command = buildOntopCommand("bootstrap --base-iri " + baseIRI, null, owlFileName, "standard");

        // Extract database schema
        extractDBSchema(baseIRI);

        // Execute the command
        executeCommand(command);

        // Return the content of the mapping file
        return readMappingFileContent();
    }

    public String readConceptNameFile() {
        String TMP_ConceptFileName = buildFilePath(conceptFileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(TMP_ConceptFileName))) {
            StringBuilder contentBuilder = new StringBuilder();
            reader.lines().forEach(line -> contentBuilder.append(line).append("\n"));
            return contentBuilder.toString();
        } catch (IOException e) {
            System.err.println("Error reading concept names file: " + e.getMessage());
            return "Error reading concept names file.";
        }

    }

    ///////////////////////////
    //////Mapping Methods//////
    ///////////////////////////

    private void genMappingSimFile() {
        // get all concept in mapping file into list
        HashSet<String> conceptInDatabase = extractConceptInMappingFile(baseIRI);
//        System.out.println(conceptInDatabase.toString());
        ArrayList<SymmetricPair<String>> swapConcept = filterRewritingConcept(conceptInDatabase);
        ArrayList<String> textToAppend = genSimMappingValue(swapConcept);
        addTextEOF(textToAppend);
    }
    public ArrayList<SymmetricPair<String>> filterRewritingConcept(HashSet<String> conceptInDatabase) {
        ArrayList<SymmetricPair<String>> swapConcept = new ArrayList<>();
        ArrayList<SymmetricPair<String>> generateConcept = new ArrayList<>();
        int checker = 0;
        // 0 mean concepts not exist -> ignore this similarity
        // 1 mean concepts exist one -> generate mapping from database
        // 2 mean concepts exist two -> swap concept in mapping file

        for (SymmetricPair<String> concept: rewritingConcept) {
            if(conceptInDatabase.contains(concept.getFirst())) checker++;
            if(conceptInDatabase.contains(concept.getSecond())) checker++;
            switch(checker) {
                case 1:
                    generateConcept.add(concept);
                    break;
                case 2:
                    swapConcept.add(concept);
                    break;
                default:
                    // code block
                    break;
            }
            checker = 0;
        }
        return swapConcept;
    }
    public HashMap<String, HashMap<String,String>> extractMappingValueFile(){
        HashMap<String, HashMap<String,String>> mappingIdMap = new HashMap<>();

        String line;
        String currentMappingId = null;
        String currentTarget = null;
        String currentSource = null;

        try {
            // Read file
            String filePath = buildFilePath(mappingFileName);
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            // Skip header part
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("mappingId")) {
                    break;
                }
            }

            // Save value into hashmap
            do {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] value = line.split("\t+");
                switch (value[0]) {
                    case "mappingId":
                        // Save previous mapping if exists
                        if (currentMappingId != null && currentTarget != null && currentSource != null) {
                            HashMap<String, String> targetSourceMap = new HashMap<>();
                            targetSourceMap.put("target", currentTarget);
                            targetSourceMap.put("source", currentSource);
                            mappingIdMap.put(currentMappingId, targetSourceMap);
                        }
                        // Start new mapping
                        currentMappingId = value[1].trim();
                        currentTarget = null;
                        currentSource = null;
                        break;

                    case "target":
                        currentTarget = value[1].trim();
                        break;

                    case "source":
                        currentSource = value[1].trim();
                        break;
                }
            } while ((line = br.readLine()) != null);
            // Save last mapping if exists
            if (currentMappingId != null && currentTarget != null && currentSource != null) {
                HashMap<String, String> targetSourceMap = new HashMap<>();
                targetSourceMap.put("target", currentTarget);
                targetSourceMap.put("source", currentSource);
                mappingIdMap.put(currentMappingId, targetSourceMap);
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mappingIdMap;
    }

    public HashSet<String> extractConceptInMappingFile(String baseIRI) {
        HashSet<String> conceptInMapping = new HashSet<>();
        HashMap<String, HashMap<String,String>> mappingIdMap = extractMappingValueFile();
        // assume concepts are after 'a' relationship
        String keyword = "a";

        // Define the regex pattern to find the value after the keyword
        String regex = keyword + "\\s*<([^>]+)>";

        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);

        for (Map.Entry<String, HashMap<String, String>> outerEntry : mappingIdMap.entrySet()) {
            HashMap<String, String> innerMap = outerEntry.getValue();
            // Match the pattern against the input string
            Matcher matcher = pattern.matcher(innerMap.get("target"));

            // Check if a match is found and print the result
            if (matcher.find()) {
                String extractedValue = matcher.group(1);

                // Remove the base IRI from the extracted value
                if (extractedValue.startsWith(baseIRI)) {
                    String result = extractedValue.substring(baseIRI.length());
                    conceptInMapping.add(result);
                } else {
                    System.out.println("The extracted value does not start with the base IRI.");
                }
            }
        }
        return conceptInMapping;
    }

    public ArrayList<String> genSimMappingValue(ArrayList<SymmetricPair<String>> swapPair) {

        int counter = 1;
        ArrayList<String> textToAppend = new ArrayList<>();
        HashMap<String, HashMap<String,String>> mappingIdMap = extractMappingValueFile();
        makeFullMap(swapPair);

        for (Map.Entry<String, HashMap<String, String>> outerEntry : mappingIdMap.entrySet()) {
            HashMap<String, String> innerMap = outerEntry.getValue();
            for (SymmetricPair<String> concept : swapPair) {
                if (innerMap.get("target").contains(concept.getFirst())) {
                    String tempString = innerMap.get("target").replace(concept.getFirst(), concept.getSecond());
//                    System.out.println(tempString);

                    String simMapping = "mappingId\tMAPPING-SIM-ID" + counter++ + "\n" +
                            "target\t\t" + tempString + "\n" +
                            "source\t\t" + innerMap.get("source");
                    textToAppend.add(simMapping);

                    break;
                }
            }
        }
        return textToAppend;
    }

    public void extractDBSchema (String baseIRI) {
        String filePath = buildFilePath("dbSchema.json");
        HashMap<String, List<String>> dbSchema = new HashMap<>();
        HashMap<String, HashMap<String,String>> mappingIdMap = extractMappingValueFile();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            for (Map.Entry<String, HashMap<String, String>> outerEntry : mappingIdMap.entrySet()) {
                HashMap<String, String> innerMap = outerEntry.getValue();
                if(innerMap.get("source").contains("*")){
                    extractTableColumnsInfo(dbSchema, baseIRI, innerMap.get("target"));
                }
            }

            objectMapper.writeValue(new File(filePath), dbSchema);
            System.out.println("JSON file created: dbSchema.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void extractTableColumnsInfo (HashMap<String, List<String>> dbSchema, String baseIRI, String input) {

        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String fullIRI = matcher.group(1);
            if (fullIRI.startsWith(baseIRI)) {
                String relativeIRI = fullIRI.substring(baseIRI.length());

                // Split the relative IRI into main part and hash part if present
                String[] parts = relativeIRI.split("#");
                String key = parts[0].split("/")[0];
                String value = parts.length > 1 ? parts[1] : null;

                if (!dbSchema.containsKey(key)) {
                    dbSchema.put(key, new ArrayList<>());
                }

                if (value != null) {
                    dbSchema.get(key).add(value);
                }

            }
        }

    }

    public void addTextEOF(ArrayList<String> textToAppend) {
        String originalFilePath = buildFilePath(mappingFileName);
        String newFilePath = originalFilePath.replace(".obda", "_sim.obda");

        try {
            // Read the content of the original file
            List<String> lines = Files.readAllLines(Paths.get(originalFilePath));

            // Create the new content
            StringBuilder newContent = new StringBuilder();

            // Add all lines except the last one
            for (int i = 0; i < lines.size() - 1; i++) {
                newContent.append(lines.get(i)).append(System.lineSeparator());
            }
            // Append the new text
            for (String s : textToAppend) {
                newContent.append(System.lineSeparator()).append(s).append(System.lineSeparator());
            }

            // Append the last line
            if (!lines.isEmpty()) {
                newContent.append(lines.get(lines.size() - 1));
            }

            // Write the new content to a new file
            Path newFile = Paths.get(newFilePath);
            if (Files.exists(newFile)) {
                Files.delete(newFile);
                System.out.println("Existing file deleted: " + newFilePath);
            }
            Files.write(newFile, newContent.toString().getBytes(), StandardOpenOption.CREATE);

            System.out.println("Text appended above the last line and written to new file successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeFullMap(ArrayList<SymmetricPair<String>> map){
        ArrayList<SymmetricPair<String>> reversedMap = new ArrayList<>();
        for (SymmetricPair<String> pair : map) {
            reversedMap.add(new SymmetricPair<>(pair.getSecond(), pair.getFirst()));
        }
        for (SymmetricPair<String> pair : reversedMap) {
            map.add(new SymmetricPair<>(pair.getFirst(), pair.getSecond()));
        }
    }

}
