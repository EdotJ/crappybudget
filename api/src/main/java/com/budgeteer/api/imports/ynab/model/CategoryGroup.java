package com.budgeteer.api.imports.ynab.model;

import java.util.List;

public class CategoryGroup {

    private String id;
    private String name;
    private List<YnabResponseCategory> categories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<YnabResponseCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<YnabResponseCategory> categories) {
        this.categories = categories;
    }
}
