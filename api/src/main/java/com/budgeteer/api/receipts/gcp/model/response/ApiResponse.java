package com.budgeteer.api.receipts.gcp.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ApiResponse {

    @JsonProperty("responses")
    private List<Response> responses;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String base64Encoding;

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public String getBase64Encoding() {
        return base64Encoding;
    }

    public void setBase64Encoding(String base64Encoding) {
        this.base64Encoding = base64Encoding;
    }
}
