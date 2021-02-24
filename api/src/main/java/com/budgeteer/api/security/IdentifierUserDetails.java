package com.budgeteer.api.security;

import io.micronaut.security.authentication.UserDetails;

import java.util.Collection;

public class IdentifierUserDetails extends UserDetails {

    private Long id;

    public IdentifierUserDetails(String username, Collection<String> roles, Long id) {
        this(username, roles);
        this.id = id;
    }

    public IdentifierUserDetails(String username, Collection<String> roles) {
        super(username, roles);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
