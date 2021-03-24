package com.budgeteer.api.dto.account;

import com.budgeteer.api.model.Account;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

public class SingleAccountDto {
    private Long id;
    private String name;
    private Long userId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal balance;

    public SingleAccountDto() {
    }

    public SingleAccountDto(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.userId = account.getUser().getId();
        if (account.getBalance() != null) {
            this.balance = account.getBalance();
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
