package com.budgeteer.api.receipts.gcp.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Feature {

    @JsonProperty("type")
    private DetectionType type;

    public DetectionType getType() {
        return type;
    }

    public void setType(DetectionType type) {
        this.type = type;
    }
}