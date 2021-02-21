package com.budgeteer.api.dto.account;

import com.budgeteer.api.model.Account;

public class SingleAccountDto {
    private Long id;
    private String name;
    private Long userId;

    public SingleAccountDto() {
    }

    public SingleAccountDto(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.userId = account.getUser().getId();
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
}
