//package io.github.vkgsim.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(RestController.class)
//public class RestControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private OntopController ontopController;
//
//    @MockBean
//    private SimilarityController similarityController;
//
//    @BeforeEach
//    void setUp() {
//        // Initialize mocks created with @Mock annotation
//        MockitoAnnotations.openMocks(this);
//    }
//
//    /**
//     * Test the createUserFolder method
//     */
//    @Test
//    public void testCreateUserFolder() throws Exception {
//        String username = "testUser";
//        when(ontopController.createUserFolder()).thenReturn("User folder created.");
//
//        mockMvc.perform(get("/createUserFolder")
//                        .param("username", username))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User folder created."));
//    }
//
//    /**
//     * Test the uploadFile method
//     */
//    @Test
//    public void testUploadFile() throws Exception {
//        MockMultipartFile owlFile = new MockMultipartFile("owlFile", "test.owl", MediaType.TEXT_PLAIN_VALUE, "test content".getBytes());
//        MockMultipartFile mappingFile = new MockMultipartFile("mappingFile", "mapping.map", MediaType.TEXT_PLAIN_VALUE, "mapping content".getBytes());
//        MockMultipartFile propertiesFile = new MockMultipartFile("propertiesFile", "props.properties", MediaType.TEXT_PLAIN_VALUE, "props content".getBytes());
//        MockMultipartFile driverFile = new MockMultipartFile("driverFile", "driver.jar", MediaType.APPLICATION_OCTET_STREAM_VALUE, "driver content".getBytes());
//
//        mockMvc.perform(multipart("/uploadFile")
//                        .file(owlFile)
//                        .file(mappingFile)
//                        .file(propertiesFile)
//                        .file(driverFile))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Files uploaded successfully."));
//    }
//
//    /**
//     * Test the FindAllConceptNames method
//     */
//    @Test
//    public void testFindAllConceptNames() throws Exception {
//        List<String> conceptNames = List.of("ConceptName");
//        when(ontopController.retrieveConceptName(Mockito.any(File.class))).thenReturn(conceptNames);
//
//        mockMvc.perform(get("/findAllConceptNames"))
//                .andExpect(status().isOk())
//                .andExpect(content().json("[\"ConceptName\"]"));
//    }
//
//    /**
//     * Test the SendQuery method
//     */
//    @Test
//    public void testSendQuery() throws Exception {
//        String query = "SELECT * WHERE { ?s ?p ?o }";
//        String owlFileType = "owl";
//        when(ontopController.ontopQuery(query, owlFileType, "asd")).thenReturn("Query result");
//
//        mockMvc.perform(post("/sendQuery")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"query\":\"SELECT * WHERE { ?s ?p ?o }\", \"owlFileType\":\"owl\"}"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Query result"));
//    }
//
//    /**
//     * Test the generateMapping method
//     */
//    @Test
//    public void testGenerateMapping() throws Exception {
//        String baseIRI = "http://example.com/baseIRI";
//        when(ontopController.ontopBootstrap(baseIRI)).thenReturn("Mapping generated");
//
//        mockMvc.perform(get("/generateMapping")
//                        .param("baseIRI", baseIRI))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Mapping generated"));
//    }
//
//    /**
//     * Test the saveMapping method
//     */
//    @Test
//    public void testSaveMapping() throws Exception {
//        String mapping = "mapping content";
//        when(ontopController.saveMapping(mapping)).thenReturn("Mapping saved");
//
//        mockMvc.perform(post("/saveMapping")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"mapping\":\"mapping content\"}"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Mapping saved"));
//    }
//
//    /**
//     * Test the ReadMappingFileContent method
//     */
//    @Test
//    public void testReadMappingFileContent() throws Exception {
//        when(ontopController.readMappingFileContent()).thenReturn("Mapping file content");
//
//        mockMvc.perform(get("/readMappingFileContent"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Mapping file content"));
//    }
//
//    /**
//     * Test the ReadSimilarityFileContent method
//     */
//    @Test
//    public void testReadSimilarityFileContent() throws Exception {
//        when(ontopController.readSimilarityFileContent()).thenReturn("Similarity file content");
//
//        mockMvc.perform(get("/readSimilarityFileContent"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Similarity file content"));
//    }
//
//    /**
//     * Test the ReadConceptNameFile method
//     */
//    @Test
//    public void testReadConceptNameFile() throws Exception {
//        when(ontopController.readConceptNameFile()).thenReturn("Concept name file content");
//
//        mockMvc.perform(get("/readConceptNameFile"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Concept name file content"));
//    }
//
//    /**
//     * Test the SimilarityMeasureAllConcept method
//     */
//    @Test
//    public void testSimilarityMeasureAllConcept() throws Exception {
//        when(similarityController.readAllConceptWithThreshold()).thenReturn("All concept pairs similarity");
//
//        mockMvc.perform(get("/similarityMeasureAllConcept"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("All concept pairs similarity"));
//    }
//
//    /**
//     * Test the GetOWLFilename method
//     */
//    @Test
//    public void testGetOWLFilename() throws Exception {
//        ArrayList<String> owlFilenames = new ArrayList<>(List.of("test.owl"));
//        when(ontopController.getOWLFileNameWithBoostrap()).thenReturn(owlFilenames);
//
//        mockMvc.perform(get("/getOWLFilename"))
//                .andExpect(status().isOk())
//                .andExpect(content().json("[\"test.owl\"]"));
//    }
//
//}
