package com.budgeteer.api.receipts.gcp.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class BoundingPoly {

    @JsonProperty("vertices")
    private List<Vertex> vertices;

    public List<Vertex> getVertices() {
        if (vertices == null) {
            vertices = new ArrayList<>();
        }
        return vertices;
    }
}