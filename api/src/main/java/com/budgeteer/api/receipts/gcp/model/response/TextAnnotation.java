package com.budgeteer.api.receipts.gcp.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TextAnnotation {

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("description")
    private String description;

    @JsonProperty("boundingPoly")
    private BoundingPoly boundingPoly;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BoundingPoly getBoundingPoly() {
        return boundingPoly;
    }

    public void setBoundingPoly(BoundingPoly boundingPoly) {
        this.boundingPoly = boundingPoly;
    }
}
