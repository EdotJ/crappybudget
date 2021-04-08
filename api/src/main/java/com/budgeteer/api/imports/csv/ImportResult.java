package com.budgeteer.api.imports.csv;

import com.budgeteer.api.imports.ImportEntry;

import java.util.List;

public class ImportResult {

    private List<ImportEntry> importedEntries;
    private int skippedLines;

    public ImportResult(List<ImportEntry> importedEntries, int skippedLines) {
        this.importedEntries = importedEntries;
        this.skippedLines = skippedLines;
    }

    public List<ImportEntry> getImportedEntries() {
        return importedEntries;
    }

    public void setImportedEntries(List<ImportEntry> importedEntries) {
        this.importedEntries = importedEntries;
    }

    public int getTotalLines() {
        return skippedLines + importedEntries.size();
    }

    public int getSkippedLines() {
        return skippedLines;
    }

    public void setSkippedLines(int skippedLines) {
        this.skippedLines = skippedLines;
    }
}
