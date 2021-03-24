package com.budgeteer.api.dto.user;

import com.budgeteer.api.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;


public class SingleUserDto {

    private Long id;
    private String email;
    private String username;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;

    public SingleUserDto() {
    }

    public SingleUserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
    }

    public SingleUserDto(Long id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
