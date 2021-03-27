package com.budgeteer.api.receipts.gcp.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Image {

    @JsonProperty("content")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}