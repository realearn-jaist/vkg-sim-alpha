package io.github.vkgsim.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.vkgsim.model.OntopModel;
import io.github.vkgsim.util.SymmetricPair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.StandardOpenOption;

@Component
public class OntopController {

    private Set<String> processedProperties = new HashSet<>(); // Set to store processed properties
    private ArrayList<SymmetricPair<String>> rewritingConcept = new ArrayList<>();
    private String baseIRI;

    @Autowired
    private OntopModel ontopModel;

    /**
     * Initialize the OntopController and OntopModel value.
     * Save the uploaded files to the user folder.
     * 
     * @param owlFile owl file
     * @param mappingFile mapping file
     * @param propertiesFile properties file
     * @param driverFile driver file
     * @throws IOException
     * @throws OWLOntologyCreationException
     */
    public void initial(MultipartFile owlFile, MultipartFile mappingFile, MultipartFile propertiesFile,
            MultipartFile driverFile) throws IOException, OWLOntologyCreationException {
        // set owl file name
        String owlFileName = owlFile.getOriginalFilename();
        owlFileName = "ontology." + getFileExtension(owlFileName);

        // extract baseIRI here
        if (getFileExtension(owlFileName).equals("owl")) {
            baseIRI = extractBaseIRI(owlFile);
        } else {
            baseIRI = "http://";
        }
        System.out.println(baseIRI);

        // set mapping file name
        String mappingFileName = mappingFile.getOriginalFilename();
        String propertiesFileName = propertiesFile.getOriginalFilename();
        String driverFileName = driverFile.getOriginalFilename();

        ontopModel.setOwlFile(owlFileName);
        ontopModel.setMappingFileName(mappingFileName);
        ontopModel.setPropertiesFileName(propertiesFileName);

        // create dir for upload file
        Path uploadDirPath = ontopModel.getUploadDirPath();
        Files.createDirectories(uploadDirPath);

        // save value into cache
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("baseIRI", baseIRI);
        jsonObject.put("mappingFile", mappingFileName);
        FileWriter file = new FileWriter(uploadDirPath + "/cache.json");
        file.write(jsonObject.toString());
        file.close();

        // save owl
        File savedFileOwl = new File(uploadDirPath.toFile(), owlFileName);
        owlFile.transferTo(savedFileOwl.toPath());

        // save mapping
        File savedFileMapping = new File(uploadDirPath.toFile(), mappingFileName);
        mappingFile.transferTo(savedFileMapping.toPath());

        // save properties
        File savedFilePropertiesFile = new File(uploadDirPath.toFile(), propertiesFileName);
        propertiesFile.transferTo(savedFilePropertiesFile.toPath());

        // create dir for upload driver
        // save driver
        Path uploadDriverDirPath = ontopModel.getUploadDriverDirPath();
        Files.createDirectories(uploadDirPath);

        File savedFileDriverFile = new File(uploadDriverDirPath.toFile(), driverFileName);
        driverFile.transferTo(savedFileDriverFile.toPath());

    }

    /**
     * Extract the base IRI from the OWL file
     * 
     * @param owlFile owl file
     * @return BaseIRI base IRI from the OWL file
     * @throws OWLOntologyCreationException
     * @throws IOException
     */
    private String extractBaseIRI(MultipartFile owlFile) throws OWLOntologyCreationException, IOException {
        // Create an OWL ontology manager for load the OWL file
        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();

        // Convert MultipartFile to File
        File convFile = new File(owlFile.getOriginalFilename());
        convFile.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(owlFile.getBytes());
        }

        // Extract the base IRI from the OWL file
        IRI documentIRI = IRI.create(convFile);
        OWLOntology owlOntology = owlOntologyManager.loadOntologyFromOntologyDocument(documentIRI);
        String result = owlOntology
                .getOntologyID()
                .getDefaultDocumentIRI()
                .toQuotedString();
        return result.substring(1, result.length() - 1);
    }

    ///////////////////////////
    //// Setters and Getters////
    ///////////////////////////

    /**
     * Get the OntopModel
     * 
     * @return ontopModel
     */
    public OntopModel getontopModel() {
        return ontopModel;
    }

    /**
     * Set username and load value from cache
     * 
     * @param username username
     */
    public void setUsername(String username) {
        ontopModel.setUsername(username);
        directoryPathReader(ontopModel.getFilePath());

        try {
            baseIRI = loadCache().get("baseIRI");
        } catch (IOException e) {
        }

    }

    /**
     * Load importance value from cache
     *
     * @return cache that kept importance values
     * @throws IOException
     */
    public Map<String, String> loadCache() throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(ontopModel.getFilePath("cache.json"));
            Map<String, String> map = mapper.readValue(file, Map.class);
            return map;
        } catch (IOException e) {
            throw new IOException();
        }
    }

    /**
     * Add the file name to the list if it exists
     * @param fileNames list of file names
     * @param fileName file name
     *
     * @throws NullPointerException
     */
    private void addFileNameIfExists(List<String> fileNames, String fileName) throws NullPointerException {
        Path filePath = Paths.get(ontopModel.getFilePath(fileName));
        if (Files.exists(filePath)) {
            fileNames.add(filePath.getFileName().toString());
        }
    }

    /**
     * Get all the OWL file names
     * 
     * @return fileNames all the OWL file names
     */
    public ArrayList<String> getOWLFileNameWithBoostrap() throws NullPointerException {
        ArrayList<String> fileNames = new ArrayList<>();
        String owlFileName = ontopModel.getOwlFileName();
        addFileNameIfExists(fileNames, owlFileName);
        addFileNameIfExists(fileNames, ontopModel.getBootstrapFileName(owlFileName));
        return fileNames;
    }

    /**
     * function to read all the file name in directory
     * 
     * @param directoryPath path of the directory
     */
    private void directoryPathReader(String directoryPath) {

        // Create a File object for the directory
        File directory = new File(directoryPath);

        // Check if the directory exists and is indeed a directory
        if (directory.exists() && directory.isDirectory()) {
            // List all files in the directory
            File[] files = directory.listFiles();

            if (files != null && files.length > 0) {
                if (Arrays.stream(files).anyMatch(file -> file.getName().equals("ontology.owl"))) {
                    ontopModel.setOwlFile("ontology.owl");
                } else {
                    ontopModel.setOwlFile("ontology.krss");
                }

                try {
                    ontopModel.setMappingFileName(loadCache().get("mappingFile"));
                    System.out.println();
                    System.out.println(ontopModel.getMappingFilePath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for (File file : files) {
                    // Check if the file is a file (not a directory)
                    if (file.isFile()) {
                        // Get the file name
                        String fileName = file.getName();

                        // Get the file extension
                        String fileExtension = getFileExtension(fileName);

                        switch (fileExtension) {
                            case "properties":
                                ontopModel.setPropertiesFileName(fileName);
                                break;
                        }
                    }
                }
            } else {
                System.out.println("The directory is empty.");
            }
        } else {
            System.out.println("The specified path is not a directory or does not exist.");
        }
    }

    /**
     * Get the extension of the file
     * 
     * @param fileName file name
     * @return extension of the file
     */
    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return ""; // No extension
        }
    }

    ///////////////////////////
    /////// Ontop Methods//////
    ///////////////////////////

    /**
     * Prepare the OWL file
     * 
     * @param owlFile owl file
     * @return ontology file
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
     * 
     * @param mapping mapping file
     * @return mapping file after user edit
     */
    public String saveMapping(String mapping) {
        String TMP_MappingFileName = ontopModel.getMappingFilePath();
        try (FileWriter writer = new FileWriter(TMP_MappingFileName)) {
            writer.write(mapping);
        } catch (IOException e) {
            System.err.println("Failed to save mapping: " + e.getMessage());
            return "Error saving mapping file.";
        }
        return mapping;
    }

    /**
     * Retrieve object properties for a given OWL class
     * 
     * @param ontology ontology
     * @param owlClass owl class
     * @param shortFormProvider short form provider
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
     * Retrieve the concept names
     * 
     * @return list of concept names
     */
    public List<String> retrieveConceptName() {
        String owlFilePath = ontopModel.getOwlFileDirFilePath();
        File owlFile = new File(owlFilePath);
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
        String TMP_ConceptFileName = ontopModel.getConceptNamesFilePath();
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
     * Read similarity concepts from file
     * 
     * @return similarity concepts as json array
     * @throws IOException
     */
    public JSONArray readSimilarityFileContent() throws IOException {
        String TMP_MappingFileName = ontopModel.getSimilarityFilePath();
        try (BufferedReader reader = new BufferedReader(new FileReader(TMP_MappingFileName))) {
            JSONArray similaritiesArray = new JSONArray();

            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming each block of lines pertains to one comparison
                String[] lines = line.split("\\|");
                if (lines.length >= 8) {
                    JSONObject similarityEntry = new JSONObject();
                    similarityEntry.put("concept1", lines[0].trim());
                    similarityEntry.put("concept2", lines[1].trim());
                    similarityEntry.put("similarity", lines[2].trim());
                    similarityEntry.put("forward_explanation", lines[3].trim());
                    similarityEntry.put("backward_explanation", lines[4].trim());
                    similarityEntry.put("summary_explanation", lines[5].trim());
                    similarityEntry.put("description_tree1", new JSONObject(lines[6].trim()));
                    similarityEntry.put("description_tree2", new JSONObject(lines[7].trim()));

                    similaritiesArray.put(similarityEntry);
                }
            }

            return similaritiesArray;
        } catch (IOException e) {
            System.err.println("Error reading concept Similarity file: " + e.getMessage());
            throw new IOException("Error reading concept Similarity file.", e);
        }
    }

    /**
     * Save the similarity concepts to rewritingConcept for rewriting
     * 
     * @param result string of similarity concepts
     */
    public void saveSimResult(String result) {
        System.out.println("result: " + result);
        try {
            rewritingConcept.clear();
            String[] lines = result.split("\n");
            for (String line : lines) {
                // Split the line into parts
                String[] parts = line.split(",");

                // Extract Concept 1, Concept 2
                String concept1 = parts[0];
                String concept2 = parts[1];

                // Add to HashMap
                SymmetricPair<String> simConcept = new SymmetricPair<>(concept1, concept2);
                rewritingConcept.add(simConcept);

            }
        } catch (Exception e) {
            throw new IndexOutOfBoundsException();
        }
        // System.out.println("rewritingConcept: " + rewritingConcept);
    }

    /**
     * read the mapping source and target from file
     * 
     * @return mapping of source and target as a String
     * @throws IOException
     */
    public String readMappingFileContent() throws IOException {
        String TMP_MappingFileName = ontopModel.getMappingFilePath();
        try (BufferedReader reader = new BufferedReader(new FileReader(TMP_MappingFileName))) {
            StringBuilder contentBuilder = new StringBuilder();
            reader.lines().forEach(line -> contentBuilder.append(line).append("\n"));
            return contentBuilder.toString();
        } catch (IOException e) {
            // Log the error and rethrow the exception with additional context
            System.err.println("Error reading mapping file: " + e.getMessage());
            throw new IOException("Failed to read mapping file at " + TMP_MappingFileName, e);
        }
    }

    /**
     * Create a user folder
     * 
     * @return user folder path
     */
    public String createUserFolder() {
        Path userFolder = ontopModel.getPath("inputFiles", ontopModel.getUsername());
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
     * 
     * @param command command
     * @return result after run the command as a String
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
     * 
     * @param directory directory
     * @param content content
     * @param prefix prefix
     * @param suffix suffix
     * @return temporary file
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
     * 
     * @param action action
     * @param queryFileName name of the query file
     * @param owlType type of owl
     * @return Ontop command as a String
     */
    private String buildOntopCommand(String action, String queryFileName, String owlType, String queryType) {

        // load file path for Ontop command
        String TMP_MappingFileName = ontopModel.getMappingFilePath();
        String TMP_OWLFileName = ontopModel.getOwlFileDirFilePath();
        String TMP_PropertiesFileName = ontopModel.getPropertiesFilePath();

        // Transform the file paths for the CLI
        TMP_MappingFileName = ontopModel.stringTransformForCLI(TMP_MappingFileName);
        TMP_OWLFileName = ontopModel.stringTransformForCLI(TMP_OWLFileName);
        TMP_PropertiesFileName = ontopModel.stringTransformForCLI(TMP_PropertiesFileName);

        // Read similarity mapping when search with similarity
        if (queryType.equals("similarity")) {
            System.out.println("Similarity search");
            TMP_MappingFileName = TMP_MappingFileName.replace(".obda", "_sim.obda");
        }

        // Read owl file that generate from bootstrap
        if (owlType.equals("Bootstrap")) {
            TMP_OWLFileName = TMP_OWLFileName.substring(0, TMP_OWLFileName.lastIndexOf('.')) + "_tmp.owl";
        }

        return String.format("ontop %s -m %s -t %s -p %s %s", action, TMP_MappingFileName, TMP_OWLFileName,
                TMP_PropertiesFileName,
                queryFileName != null ? "-q " + queryFileName : "");
    }

    /**
     * Delete a temporary file
     * 
     * @param file file to delete
     */
    public void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * Execute a SPARQL query
     * 
     * @param sparqlQuery sparql query
     * @param owlFileType owl file type
     * @param queryType query type
     * @return result after run the query
     * @throws IOException
     */
    public String ontopQuery(String sparqlQuery, String owlFileType, String queryType) throws IOException {
        Path ontopCliDir = ontopModel.getPath();
        try {
            // Create a temporary SPARQL query file
            File tempQueryFile = createTempFile(ontopCliDir, sparqlQuery, "sparqlQuery", ".txt");

            String owlType = (owlFileType.equals("ontology_tmp.owl")) ? "Bootstrap" : "Normal";
            if (queryType.equals("similarity")) {
                genMappingSimFile();
            }
            // Build command with temporary file
            String command = buildOntopCommand("query", tempQueryFile.getName(), owlType, queryType);

            // Execute command and handle result
            try {
                return executeCommand(command);
            } catch (Exception e) {
                // Ensure the temporary file is deleted
                deleteTempFile(tempQueryFile);
                throw e;
            } finally {
                // Ensure the temporary file is deleted
                deleteTempFile(tempQueryFile);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Generate mapping file from bootstrap
     * 
     * @param baseIRI base IRI
     * @return mapping of source and target as a String
     */
    public String ontopBootstrap(String baseIRI) {
        this.baseIRI = baseIRI;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("baseIRI", baseIRI);
            jsonObject.put("mappingFile", loadCache().get("mappingFile"));
            FileWriter file = new FileWriter(ontopModel.getUploadDirPath() + "/cache.json");
            file.write(jsonObject.toString());
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Build the command for bootstrapping
        String command = buildOntopCommand("bootstrap --base-iri " + baseIRI, null, "Bootstrap", "standard");

        // Execute the command
        executeCommand(command);

        // Extract database schema
        extractDBSchema(baseIRI);

        // Return the content of the mapping file
        try {
            return readMappingFileContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read concepts name in file
     * 
     * @return all concepts name in file
     */
    public String readConceptNameFile() {
        String TMP_ConceptFileName = ontopModel.getConceptNamesFilePath();
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
    ////// Mapping Methods//////
    ///////////////////////////

    /**
     * Generate the similarity mapping file for similarity search
     */
    private void genMappingSimFile() {
        // get all concept in mapping file into list
        HashSet<String> conceptInDatabase = extractConceptInMappingFile(baseIRI);
        List<ArrayList<SymmetricPair<String>>> rewritingValue = filterRewritingConcept(conceptInDatabase);
        ArrayList<String> textToAppend = genSimMappingValue(rewritingValue);
        addTextEOF(textToAppend);
    }

    /**
     * Filter the rewriting concept which concept can swap or which concept must generate source from database schema
     * 
     * @param conceptInDatabase all concepts that found on database which extract from mapping file
     * @return list of swap concept and generate concept
     */
    public List<ArrayList<SymmetricPair<String>>> filterRewritingConcept(HashSet<String> conceptInDatabase) {
        ArrayList<SymmetricPair<String>> swapConcept = new ArrayList<>();
        ArrayList<SymmetricPair<String>> generateConcept = new ArrayList<>();
        int checker = 0;
        // 0 mean concepts not exist -> ignore this similarity
        // 1 mean concepts exist one -> generate mapping from database
        // 2 mean concepts exist two -> swap concept in mapping file

        for (SymmetricPair<String> concept : rewritingConcept) {
            if (conceptInDatabase.contains(concept.getFirst()))
                checker++;
            if (conceptInDatabase.contains(concept.getSecond())) {
                concept.swap();
                checker++;
            }
            switch (checker) {
                case 1:
                    concept.swap();
                    generateConcept.add(concept);
                    break;
                case 2:
                    swapConcept.add(concept);
                    break;
                default:
                    break;
            }
            checker = 0;
        }
        return Arrays.asList(swapConcept, generateConcept);
    }

    /**
     * Extract source and target from the mapping file
     * 
     * @return hashmap which contains source and target as a key and value respectively
     */
    public HashMap<String, HashMap<String, String>> extractMappingValueFile() {
        HashMap<String, HashMap<String, String>> mappingIdMap = new HashMap<>();

        String line;
        String currentMappingId = null;
        String currentTarget = null;
        String currentSource = null;

        try {
            // Read file
            String filePath = ontopModel.getMappingFilePath();
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

    /**
     * Extract the concept in the mapping file
     * 
     * @param baseIRI base IRI
     * @return all concepts that appear in mapping file
     */
    public HashSet<String> extractConceptInMappingFile(String baseIRI) {
        HashSet<String> conceptInMapping = new HashSet<>();
        HashMap<String, HashMap<String, String>> mappingIdMap = extractMappingValueFile();
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

    /**
     * Generate the similarity mapping value from swap concepts and generate concepts
     * 
     * @param rewritingValue all concepts that use for similarity search
     * @return String of source and target value that will append end of mapping file
     */
    public ArrayList<String> genSimMappingValue(List<ArrayList<SymmetricPair<String>>> rewritingValue) {
        ArrayList<SymmetricPair<String>> swapConceptPair = rewritingValue.get(0);
        ArrayList<SymmetricPair<String>> generateConceptPair = rewritingValue.get(1);
        int counter = 1;
        ArrayList<String> textToAppend = new ArrayList<>();
        HashMap<String, HashMap<String, String>> mappingIdMap = extractMappingValueFile();
        makeFullMap(swapConceptPair);

        for (Map.Entry<String, HashMap<String, String>> outerEntry : mappingIdMap.entrySet()) {
            HashMap<String, String> innerMap = outerEntry.getValue();
            for (SymmetricPair<String> concepts : swapConceptPair) {
                if (innerMap.get("target").contains(concepts.getFirst())) {
                    String targetString = innerMap.get("target").replace(concepts.getFirst(), concepts.getSecond());

                    String simMapping = "mappingId\tMAPPING-SIM-ID" + counter++ + "\n" +
                            "target\t\t" + targetString + "\n" +
                            "source\t\t" + innerMap.get("source");
                    textToAppend.add(simMapping);

                    break;
                }
            }
            for (SymmetricPair<String> concepts : generateConceptPair) {
                if (innerMap.get("target").contains(concepts.getFirst())) {
                    /*
                     * not implement
                     * String targetString <- craft target which use columns name from database dbSchema
                     * String sourceString <- craft source which where cause need to find that concept in value in database
                     * String simMapping = "mappingId\tMAPPING-SIM-ID" + counter++ + "\n" +
                     * "target\t\t" + targetString + "\n" +
                     * "source\t\t" + sourceString;
                     * textToAppend.add(simMapping);
                     */

                    break;
                }
            }
        }
        return textToAppend;
    }

    /**
     * Extract the database schema
     * 
     * @param baseIRI base IRI
     */
    public void extractDBSchema(String baseIRI) {
        String filePath = ontopModel.getDBSchemaFilePath();
        HashMap<String, List<String>> dbSchema = new HashMap<>();
        HashMap<String, HashMap<String, String>> mappingIdMap = extractMappingValueFile();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            for (Map.Entry<String, HashMap<String, String>> outerEntry : mappingIdMap.entrySet()) {
                HashMap<String, String> innerMap = outerEntry.getValue();
                if (innerMap.get("source").contains("*")) {
                    extractTableColumnsInfo(dbSchema, baseIRI, innerMap.get("target"));
                }
            }

            objectMapper.writeValue(new File(filePath), dbSchema);
            System.out.println("JSON file created: dbSchema.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extract the table columns information
     * 
     * @param dbSchema database schema
     * @param baseIRI base IRI
     * @param input string that want to extract
     */
    public void extractTableColumnsInfo(HashMap<String, List<String>> dbSchema, String baseIRI, String input) {

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

    /**
     * Append text to the end of the file
     * 
     * @param textToAppend text that will be used for append in file
     */
    public void addTextEOF(ArrayList<String> textToAppend) {
        // String originalFilePath = buildFilePath(mappingFileName);
        String originalFilePath = ontopModel.getMappingFilePath();
        String newFilePath = originalFilePath.replace(".obda", "_sim.obda");

        try {
            // Read the content of the original file
            List<String> lines = Files.readAllLines(Paths.get(originalFilePath));

            // Create the new content
            StringBuilder newContent = new StringBuilder();

            // Add all lines except the last one
            for (String line : lines) {
                if (line.equals("]]"))
                    break;
                newContent.append(line).append(System.lineSeparator());
            }
            // Append the new text
            for (String s : textToAppend) {
                newContent.append(System.lineSeparator()).append(s).append(System.lineSeparator());
            }

            // Append the last line
            if (!lines.isEmpty()) {
                newContent.append("]]");
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

    /**
     * Make a full map
     * i.e. make (a,b) to (a,b), (b,a)
     * @param map map that want to expand to full map
     */
    public void makeFullMap(ArrayList<SymmetricPair<String>> map) {
        ArrayList<SymmetricPair<String>> reversedMap = new ArrayList<>();
        for (SymmetricPair<String> pair : map) {
            reversedMap.add(new SymmetricPair<>(pair.getSecond(), pair.getFirst()));
        }
        for (SymmetricPair<String> pair : reversedMap) {
            map.add(new SymmetricPair<>(pair.getFirst(), pair.getSecond()));
        }
    }

    /**
     * Delete the profile
     */
    public void deleteProfile() {
        ontopModel.deleteProfile();

        processedProperties = new HashSet<>(); // Set to store processed properties
        rewritingConcept = new ArrayList<>();
        baseIRI = null;

    }

    /**
     * read BaseIRI from cache
     * @return baseIRI
     * @throws IOException
     */
    public String readBaseIRI() throws IOException {
        return loadCache().get("baseIRI");
    }

    /**
     * get username
     * @return username
     */
    public String getUsername() {
        return ontopModel.getUsername();
    }
}