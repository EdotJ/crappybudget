package com.budgeteer.api.dto.imports;

public class CsvImportResponse {

    private int totalNumberOfRows;

    private int skippedNumberOfEntries;

    public CsvImportResponse(int totalNumberOfRows, int skippedNumberOfEntries) {
        this.totalNumberOfRows = totalNumberOfRows;
        this.skippedNumberOfEntries = skippedNumberOfEntries;
    }

    public int getTotalNumberOfRows() {
        return totalNumberOfRows;
    }

    public void setTotalNumberOfRows(int totalNumberOfRows) {
        this.totalNumberOfRows = totalNumberOfRows;
    }

    public int getSkippedNumberOfEntries() {
        return skippedNumberOfEntries;
    }

    public void setSkippedNumberOfEntries(int skippedNumberOfEntries) {
        this.skippedNumberOfEntries = skippedNumberOfEntries;
    }
}
