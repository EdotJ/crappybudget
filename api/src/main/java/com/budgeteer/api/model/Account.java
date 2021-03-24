package com.budgeteer.api.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private BigDecimal balance;

    public Account() {

    }

    public Account(@NotNull String name) {
        this.name = name;
    }

    public Account(Account account, BigDecimal balance) {
        this.id = account.id;
        this.name = account.name;
        this.user = account.user;
        this.balance = balance;
    }

    public Account(Account account, int balance) {
        this.id = account.id;
        this.name = account.name;
        this.user = account.user;
        this.balance = new BigDecimal(balance);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
