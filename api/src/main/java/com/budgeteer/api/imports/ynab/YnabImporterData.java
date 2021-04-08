package com.budgeteer.api.imports.ynab;

import com.budgeteer.api.imports.ImporterData;
import com.budgeteer.api.imports.ynab.model.Budget;
import com.budgeteer.api.imports.ynab.model.CategoryGroup;
import com.budgeteer.api.imports.ynab.model.Transaction;

import java.util.List;

public class YnabImporterData implements ImporterData {

    private Budget budget;
    private List<CategoryGroup> categories;
    private List<Transaction> transactions;

    public YnabImporterData(Budget budget, List<CategoryGroup> categories, List<Transaction> transactions) {
        this.budget = budget;
        this.categories = categories;
        this.transactions = transactions;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
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
}
