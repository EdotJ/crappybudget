package com.budgeteer.api.receipts.gcp;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("cloud-vision")
public class CloudVisionConfig {

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
