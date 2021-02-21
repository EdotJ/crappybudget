package com.budgeteer.api.dto.user;

import com.budgeteer.api.dto.AbstractListDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserListDto extends AbstractListDto<SingleUserDto> {

    public UserListDto() {
        super();
    }

    public UserListDto(List<SingleUserDto> users) {
        super(users);
    }

    @JsonProperty("users")
    @Override
    protected List<SingleUserDto> getList() {
        return list;
    }
}
