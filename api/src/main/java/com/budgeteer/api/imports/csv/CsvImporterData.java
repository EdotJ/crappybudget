package com.budgeteer.api.imports.csv;

import com.budgeteer.api.imports.ImporterData;
import io.micronaut.http.multipart.CompletedFileUpload;

import java.util.Map;

public class CsvImporterData implements ImporterData {

    private Map<String, String> mappings;

    private CompletedFileUpload fileUpload;

    public CsvImporterData(Map<String, String> mappings, CompletedFileUpload fileUpload) {
        this.mappings = mappings;
        this.fileUpload = fileUpload;
    }

    public Map<String, String> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    public CompletedFileUpload getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(CompletedFileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }
}
