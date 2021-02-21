package com.budgeteer.api.dto.goal;

import com.budgeteer.api.model.Goal;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SingleGoalDto {

    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private BigDecimal value;
    private Long userId;
    private Long accountId;

    public SingleGoalDto() {

    }

    public SingleGoalDto(Goal goal) {
        this.id = goal.getId();
        this.name = goal.getName();
        this.description = goal.getDescription();
        this.date = goal.getDate();
        this.value = goal.getValue();
        this.userId = goal.getUser().getId();
        if (goal.getAccount() != null) {
            this.accountId = goal.getAccount().getId();
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
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
}
