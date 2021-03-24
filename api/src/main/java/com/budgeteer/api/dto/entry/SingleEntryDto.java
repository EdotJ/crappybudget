package com.budgeteer.api.dto.entry;

import com.budgeteer.api.model.Entry;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SingleEntryDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal value;
    private LocalDate date;
    private Boolean isExpense;
    private Long userId;
    private Long accountId;
    private Long categoryId;

    public SingleEntryDto() {
    }

    public SingleEntryDto(Entry e) {
        this.id = e.getId();
        this.name = e.getName();
        this.description = e.getDescription();
        this.value = e.getValue();
        this.date = e.getDate();
        this.isExpense = e.isExpense();
        this.userId = e.getUser().getId();
        this.accountId = e.getAccount().getId();
        if (e.getCategory() != null) {
            this.categoryId = e.getCategory().getId();
        }
    }

    public Long getId() {
        return id;
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

    @JsonProperty("isExpense")
    public Boolean isExpense() {
        return isExpense;
    }

    public void setIsExpense(Boolean expense) {
        isExpense = expense;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
