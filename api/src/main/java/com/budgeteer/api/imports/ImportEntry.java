package com.budgeteer.api.imports;

import com.budgeteer.api.model.Entry;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ImportEntry {

    private String name;

    private String description;

    @CsvNumber("###.##")
    @CsvBindByName(locale = "en")
    private BigDecimal value;

    @CsvDate("yyyy-MM-dd")
    private LocalDate date;

    private Boolean isExpense;

    private String category;

    public ImportEntry() {

    }

    public ImportEntry(String name,
                       String description,
                       BigDecimal value,
                       LocalDate date,
                       Boolean isExpense,
                       String category) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.date = date;
        this.isExpense = isExpense;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getExpense() {
        return isExpense;
    }

    public void setExpense(Boolean expense) {
        isExpense = expense;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Entry transform() {
        Entry entry = new Entry();
        entry.setName(this.name);
        entry.setDate(this.date);
        entry.setValue(this.value);
        if (this.value.compareTo(BigDecimal.ZERO) < 0) {
            entry.setIsExpense(true);
        }
        return entry;
    }
}
