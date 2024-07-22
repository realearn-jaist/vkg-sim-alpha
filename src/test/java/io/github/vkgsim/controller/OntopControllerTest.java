package io.github.vkgsim.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OntopControllerTest {

    // set up the test environment
    String testPathInput = "src/test/resources/ontop-cli/inputFiles/testUser/";
    String ontopPath = "src/test/resources/ontop-cli/";

    @InjectMocks
    private OntopController ontopController;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() throws IOException {
        //set up the controller
        ontopController.setUsername("testUser");
        ontopController.setOwlFileName("test.owl");
        ontopController.setMappingFileName("testMapping.txt");
        ontopController.setPropertiesFileName("testProperties.txt");
        ontopController.setDriverFileName("testDriver.txt");
        ontopController.setSimFileName("testSimilarity.txt");
        ontopController.setConceptFileName("testConceptNames.txt");
        ontopController.setBaseUploadDir("inputFiles/");

        //create test file
        multipartFile = new MockMultipartFile("file", "uploadedFile.txt", "text/plain", "Sample content".getBytes());
        Paths.get(testPathInput).toFile().mkdirs();
        ontopController.setOntopDir(ontopPath);

        //create test.owl file
        File owlFile = new File(testPathInput + "test.owl");
        owlFile.createNewFile();
    }

    @Test
    void testCreateUserFolder() {
        String response = ontopController.createUserFolder("testUser");
        //check if the folder is created
        assertTrue(new File(ontopPath + "inputFiles/testUser").exists());
    }

    /**
     * Test the saveUploadedFile method
     */
    @Test
    void testSaveUploadedFile() throws IOException {
        File result = ontopController.saveUploadedFile(multipartFile, "uploadedFile.txt");
        assertTrue(result.exists(), "File should exist after saving");
        assertEquals("uploadedFile.txt", result.getName());
        String content = new String(Files.readAllBytes(result.toPath()));
        assertEquals("Sample content", content, "File content should match");
    }

    /**
     * Test the PrepareOWLFile method
     */
    @Test
    void testPrepareOWLFile() throws OWLOntologyCreationException {
        File owlFile = new File(testPathInput + "test.owl");
        assertTrue(owlFile.exists());

        OWLOntology ontology = ontopController.prepareOWLFile(owlFile);
        assertNotNull(ontology);
    }

    /**
     * Test the readMappingFileContent method
     */
    @Test
    void testReadMappingFileContent() throws IOException {
        String content = "test mapping content";
        Path path = Paths.get(testPathInput + "testMapping.txt");
        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
        }

        String fileContent = ontopController.readMappingFileContent();
        assertEquals(content, fileContent.trim());
    }

    @Test
    void testReadConceptFileContent() throws IOException {
        String content = "test concept content";
        Path path = Paths.get(testPathInput + "testConceptNames.txt");
        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
        }

        String fileContent = ontopController.readConceptNameFile();
        assertEquals(content, fileContent.trim());
    }

    @Test
    void testReadSimilarityFileContent() throws IOException {
        String content = "test similarity content";
        Path path = Paths.get(testPathInput + "testSimilarity.txt");
        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
        }

        String fileContent = ontopController.readSimilarityFileContent();
        assertEquals(content, fileContent.trim());
    }

    /**
     * Test the OntopQuery method
     */
    @Test
    void testOntopQuery() throws IOException {
        String sparqlQuery = "SELECT ?s WHERE { ?s ?p ?o . }";
        String result = "query result";
        // Mocking the command execution
        OntopController spyController = spy(ontopController);
        doReturn(result).when(spyController).executeCommand(anyString());

        String response = spyController.ontopQuery(sparqlQuery, "test.owl");
        assertEquals(result, response);
    }

    /**
     * Test the OntopBootstrap method
     */
    @Test
    void testOntopBootstrap() throws IOException {
        String baseIRI = "http://example.com/base";
        String expectedResult1 = "test mapping content";
        String expectedResult2 = "test mapping content (edited)";

        // Mocking the command execution
        OntopController spyController = spy(ontopController);
        lenient().doReturn(expectedResult1).when(spyController).executeCommand(anyString());
        lenient().doReturn(expectedResult2).when(spyController).executeCommand(anyString());

        String response = spyController.ontopBootstrap(baseIRI);
        assertTrue(response.trim().equals(expectedResult1) || response.trim().equals(expectedResult2), "Response did not match expected values");
    }

    @Test
    void testCreateAndDeleteTempFile() throws IOException {
        Path ontopPathTest = Paths.get(ontopPath);
        String content = "test content";
        //create temp file
        File tempFile = ontopController.createTempFile(ontopPathTest,"test content", "sparql", ".txt");
        assertTrue(tempFile.exists());
        String fileContent = new String(Files.readAllBytes(tempFile.toPath()));
        assertEquals(content, fileContent);
        //delete temp file
        ontopController.deleteTempFile(tempFile);
        assertFalse(tempFile.exists());
    }

    @Test
    void testSaveMappingFile() throws IOException {
        String content = "test mapping content (edited)";
        Path path = Paths.get(testPathInput + "testMapping.txt");
        Files.createDirectories(path.getParent());
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(content);
        }

        ontopController.saveMapping(content);
        String fileContent = new String(Files.readAllBytes(path));
        assertEquals(content, fileContent.trim());
    }


}
