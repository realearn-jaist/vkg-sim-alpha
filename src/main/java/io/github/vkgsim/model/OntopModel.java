package io.github.vkgsim.model;

import jakarta.validation.constraints.Null;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class OntopModel {
    final private String ontopDir = "ontop-cli/";
    final private String baseUploadDir = "inputFiles/"; // Directory to store uploaded files // Name of the concept file
    final private String conceptNames = "conceptNames.txt";
    final private String SimilarityNames = "similarity.txt";
    final private String DBSchema = "dbSchema.json";
    final private String ColumnsSchema = "columnsSchema.json";

    private String propertiesFileName;

    private String mappingFileName; // Name of the mapping file

    private String username;
    private String owlFileName;
    private String fullPath;
    public void setUsername(String username) {
        this.username = username;
        this.fullPath = Paths.get(ontopDir, baseUploadDir, username, "/").toString();
    }

    public void setPropertiesFileName(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
    }

    public void setMappingFileName(String mappingFileName) {
        this.mappingFileName = mappingFileName;
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

    public String stringTransformForCLI(@NotNull String input) {
        return input.replace("\\","/").replace(ontopDir,"");
    }

    public String getFilePath() {
        return fullPath;
    }

    public String getFilePath(String fileName) throws NullPointerException{
        return Paths.get(fullPath, fileName).toString();
    }

    public String getOwlFileDirFilePath() {
        if (fullPath == null) {
            throw new NullPointerException("fullPath is null");
        }
        if (owlFileName == null) {
            throw new NullPointerException("owlFileName is null");
        }
        try {
            return Paths.get(fullPath, owlFileName).toString();
        } catch (InvalidPathException e) {
            // Handle the exception or rethrow it with a custom message
            throw new RuntimeException("Invalid path: " + e.getMessage(), e);
        }
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

    public String getColumnsSchemaFilePath() {
        return Paths.get(fullPath, ColumnsSchema).toString();
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

    public Path getUploadDriverDirPath() {
        return getPath("jdbc");
    }

    public String getBootstrapFileName(String owlFileName) {
        return owlFileName.substring(0, owlFileName.lastIndexOf('.')) + "_tmp.owl";
    }

    // Method to delete the profile directory or file.
    public boolean deleteProfile() {

        String filePath = fullPath;
        File fileOrDir = new File(filePath);

        if (fileOrDir.exists()) {
            deleteRecursively(fileOrDir);
        } else {
            System.out.println("File or directory does not exist: " + filePath);
        }

        propertiesFileName = null;
        mappingFileName = null; // Name of the mapping file
        username = null;
        owlFileName = null;
        fullPath = null;

        return true;
    }

    // Helper method to delete files and directories recursively.
    private boolean deleteRecursively(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            File[] children = fileOrDir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        System.out.println("delete: " + fileOrDir.getName());
        return fileOrDir.delete();
    }
}