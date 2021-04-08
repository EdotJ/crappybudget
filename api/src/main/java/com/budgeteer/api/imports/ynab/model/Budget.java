package com.budgeteer.api.imports.ynab.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Budget {

    private String id;

    @JsonProperty("accounts")
    private List<YnabResponseAccount> accounts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<YnabResponseAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<YnabResponseAccount> accounts) {
        this.accounts = accounts;
    }
}
