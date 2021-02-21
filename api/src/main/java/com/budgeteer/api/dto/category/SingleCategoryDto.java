package com.budgeteer.api.dto.category;

import com.budgeteer.api.model.Category;

import java.math.BigDecimal;

public class SingleCategoryDto {

    private long id;
    private String name;
    private Long parent;
    private Long userId;
    private BigDecimal budgeted;

    public SingleCategoryDto() {

    }

    public SingleCategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.userId = category.getUser().getId();
        this.budgeted = category.getBudgeted();
        if (category.getParent() != null) {
            this.parent = category.getParent().getId();
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parent;
    }

    public void setParentId(Long parent) {
        this.parent = parent;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBudgeted() {
        return budgeted;
    }

    public void setBudgeted(BigDecimal budgeted) {
        this.budgeted = budgeted;
    }
}
