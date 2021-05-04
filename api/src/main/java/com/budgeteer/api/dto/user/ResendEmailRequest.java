package com.budgeteer.api.dto.user;

public class ResendEmailRequest {

    private String email;
    private String clientHost;

    public ResendEmailRequest() {
    }

    public ResendEmailRequest(String email, String clientHost) {
        this.email = email;
        this.clientHost = clientHost;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }
}
