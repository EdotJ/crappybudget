package com.budgeteer.api.dto.imports;

public class CsvImportRequest {

    private String nameHeader;
    private String dateHeader;
    private String valueHeader;
    private String categoryHeader;
    private String isExpenseHeader;
    private String descriptionHeader;
    private Long accountId;
    private boolean addUnknownCategory = true;

    public String getNameHeader() {
        return nameHeader;
    }

    public void setNameHeader(String nameHeader) {
        this.nameHeader = nameHeader;
    }

    public String getDateHeader() {
        return dateHeader;
    }

    public void setDateHeader(String dateHeader) {
        this.dateHeader = dateHeader;
    }

    public String getValueHeader() {
        return valueHeader;
    }

    public void setValueHeader(String valueHeader) {
        this.valueHeader = valueHeader;
    }

    public String getCategoryHeader() {
        return categoryHeader;
    }

    public void setCategoryHeader(String categoryHeader) {
        this.categoryHeader = categoryHeader;
    }

    public String getIsExpenseHeader() {
        return isExpenseHeader;
    }

    public void setIsExpenseHeader(String isExpenseHeader) {
        this.isExpenseHeader = isExpenseHeader;
    }

    public String getDescriptionHeader() {
        return descriptionHeader;
    }

    public void setDescriptionHeader(String descriptionHeader) {
        this.descriptionHeader = descriptionHeader;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public boolean isAddUnknownCategory() {
        return addUnknownCategory;
    }

    public void setAddUnknownCategory(boolean addUnknownCategory) {
        this.addUnknownCategory = addUnknownCategory;
    }
}
