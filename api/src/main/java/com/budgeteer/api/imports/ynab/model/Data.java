package com.budgeteer.api.imports.ynab.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Data {

    @JsonProperty("budgets")
    private List<Budget> budgets;

    @JsonProperty("category_groups")
    private List<CategoryGroup> categories;

    @JsonProperty("transactions")
    private List<Transaction> transactions;

    @JsonProperty("default_budget")
    private Budget defaultBudget;

    public List<Budget> getBudgets() {
        return budgets;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }

    public List<CategoryGroup> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryGroup> categories) {
        this.categories = categories;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Budget getDefaultBudget() {
        return defaultBudget;
    }

    public void setDefaultBudget(Budget defaultBudget) {
        this.defaultBudget = defaultBudget;
    }
}
