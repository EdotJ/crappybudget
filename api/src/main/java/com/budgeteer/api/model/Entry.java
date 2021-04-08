package com.budgeteer.api.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "entries")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private BigDecimal value;

    @NotNull
    private LocalDate date;

    @Column(name = "is_expense")
    private boolean isExpense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public Entry() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Entry setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Entry setDescription(String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Entry setValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public Entry setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public boolean isExpense() {
        return isExpense;
    }

    public Entry setIsExpense(boolean isExpense) {
        this.isExpense = isExpense;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Entry setUser(User user) {
        this.user = user;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Entry setCategory(Category category) {
        this.category = category;
        return this;
    }

    public Account getAccount() {
        return account;
    }

    public Entry setAccount(Account account) {
        this.account = account;
        return this;
    }
}
