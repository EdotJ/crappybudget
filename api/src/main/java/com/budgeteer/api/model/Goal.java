package com.budgeteer.api.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Size(max = 140)
    private String description;

    @NotNull
    private LocalDate date;

    @NotNull
    @Column(name = "value")
    private BigDecimal currentValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "goal_type")
    private GoalType goalType;

    @NotNull
    private BigDecimal goalValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    public Goal() {

    }

    public Goal(@NotNull String name,
                @Size(max = 140) String description,
                @NotNull LocalDate date,
                @NotNull BigDecimal currentValue) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.currentValue = currentValue;
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

    public void setCurrentValue(BigDecimal value) {
        this.currentValue = value;
    }

    public BigDecimal getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(BigDecimal goalValue) {
        this.goalValue = goalValue;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
