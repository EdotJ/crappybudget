package com.budgeteer.api.dto.user;

public class ResendEmailRequest {

    private String email;

    public ResendEmailRequest() {
    }

    public ResendEmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
