package com.budgeteer.api.receipts.gcp.model.response;

public class ErrorResponse {

    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
