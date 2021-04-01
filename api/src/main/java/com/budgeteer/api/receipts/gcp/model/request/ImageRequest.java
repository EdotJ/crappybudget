package com.budgeteer.api.receipts.gcp.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ImageRequest {

    @JsonProperty("image")
    private Image image;

    @JsonProperty("features")
    private List<Feature> features;

    public ImageRequest() {

    }

    public ImageRequest(Image image, List<Feature> features) {
        this.image = image;
        this.features = features;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }
}
