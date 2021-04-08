package com.budgeteer.api.dto.imports;

public class YnabImportRequest {

    private String personalToken;

    public YnabImportRequest() {

    }

    public YnabImportRequest(String personalToken) {
        this.personalToken = personalToken;
    }

    public String getPersonalToken() {
        return personalToken;
    }

    public void setPersonalToken(String personalToken) {
        this.personalToken = personalToken;
    }
}
