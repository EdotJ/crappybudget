package com.budgeteer.api.receipts.gcp.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Request {

    @JsonProperty("requests")
    private List<ImageRequest> imageRequests;

    public Request() {
        imageRequests = new ArrayList<>();
    }

    public List<ImageRequest> getImageRequests() {
        return imageRequests;
    }

    public void setImageRequests(List<ImageRequest> imageRequests) {
        this.imageRequests = imageRequests;
    }
}
