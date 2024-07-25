package io.github.vkgsim.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class OntopModel {
    final private String ontopDir = "ontop-cli/";
    final private String baseUploadDir = "inputFiles/"; // Directory to store uploaded files // Name of the concept file
    final private String mappingFileName = "mapping.obda"; // Name of the mapping file
    final private String propertiesFileName = "properties.properties";
    final private String conceptNames = "conceptNames.txt";
    final private String SimilarityNames = "similarity.txt";
    final private String DBSchema = "dbSchema.json";

    private String username;
    private String owlFileName;

    private String fullPath;

    public void setUsername(String username) {
        this.username = username;
        this.fullPath = Paths.get(ontopDir, baseUploadDir, username, "/").toString();
    }

    public String getUsername() {
        return username;
    }

    public void setOwlFile(String owlFile) {
        this.owlFileName = owlFile;
    }

    public String getOwlFileName() {
        return owlFileName;
    }

    public String getBaseUploadDir() {
        return baseUploadDir;
    }

    public String getPropertiesFileName() {
        return propertiesFileName;
    }

    public String getMappingFileName() {
        return mappingFileName;
    }

    public String getFilePath() {
        return fullPath;
    }

    public String getFilePath(String fileName) {
        return Paths.get(fullPath, fileName).toString();
    }

    public String getOwlFileDirFilePath() {
        return Paths.get(fullPath, owlFileName).toString();
    }

    public String getOntopDirFilePath() {
        return Paths.get(fullPath, ontopDir).toString();
    }

    public String getBaseUploadDirFilePath() {
        return Paths.get(fullPath, baseUploadDir).toString();
    }

    public String getMappingFilePath() {
        return Paths.get(fullPath, mappingFileName).toString();
    }

    public String getPropertiesFilePath() {
        return Paths.get(fullPath, propertiesFileName).toString();
    }

    public String getConceptNamesFilePath() {
        return Paths.get(fullPath, conceptNames).toString();
    }

    public String getSimilarityFilePath() {
        return Paths.get(fullPath, SimilarityNames).toString();
    }

    public String getDBSchemaFilePath() {
        return Paths.get(fullPath, DBSchema).toString();
    }

    public Path getPath() {
        return Paths.get(System.getProperty("user.dir"), ontopDir);
    }

    public Path getPath(String... more) {
        return Path.of(ontopDir, more);
    }

    public Path getUploadDirPath() {
        return getPath(baseUploadDir , username);
    }

    public String getBootstrapFileName(String owlFileName) {
        return owlFileName.substring(0, owlFileName.lastIndexOf('.')) + "_tmp.owl";
    }

}
