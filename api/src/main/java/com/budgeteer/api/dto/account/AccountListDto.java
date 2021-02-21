package com.budgeteer.api.dto.account;

import com.budgeteer.api.dto.AbstractListDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AccountListDto extends AbstractListDto<SingleAccountDto> {

    public AccountListDto() {
    }

    public AccountListDto(List<SingleAccountDto> accounts) {
        super(accounts);
    }

    @JsonProperty("accounts")
    @Override
    protected List<SingleAccountDto> getList() {
        return list;
    }
}
