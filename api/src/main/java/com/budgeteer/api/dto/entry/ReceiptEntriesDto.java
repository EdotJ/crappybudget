package com.budgeteer.api.dto.entry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReceiptEntriesDto {

    private Long accountId;
    private Long categoryId;
    private LocalDate date;
    private List<ReceiptEntryDto> entries;

    public ReceiptEntriesDto() {

    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<ReceiptEntryDto> getEntries() {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        return entries;
    }

    public void setEntries(List<ReceiptEntryDto> entries) {
        this.entries = entries;
    }
}
