package com.budgeteer.api.dto.goal;

import com.budgeteer.api.model.Goal;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SingleGoalDto {

    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private BigDecimal currentValue;
    private BigDecimal goalValue;
    private Long userId;
    private Long accountId;
    private Long categoryId;
    private Long goalType;

    public SingleGoalDto() {

    }

    public SingleGoalDto(Goal goal) {
        this.id = goal.getId();
        this.name = goal.getName();
        this.description = goal.getDescription();
        this.date = goal.getDate();
        this.currentValue = goal.getCurrentValue();
        this.goalValue = goal.getGoalValue();
        this.userId = goal.getUser().getId();
        if (goal.getAccount() != null) {
            this.accountId = goal.getAccount().getId();
        }
        if (goal.getCategory() != null) {
            this.categoryId = goal.getCategory().getId();
        }
        this.goalType = goal.getGoalType().getId();
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }


    public BigDecimal getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(BigDecimal goalValue) {
        this.goalValue = goalValue;
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

    public Long getGoalType() {
        return goalType;
    }

    public void setGoalType(Long goalType) {
        this.goalType = goalType;
    }
}
