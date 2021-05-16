package com.budgeteer.api.dto.entry;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ReceiptEntryDto {

    private String name;
    private BigDecimal price;

    @JsonProperty("discount")
    private boolean isDiscount;

    public ReceiptEntryDto() {
    }

    public ReceiptEntryDto(String name, BigDecimal price) {
        this(name, price, false);
    }

    public ReceiptEntryDto(String name, BigDecimal price, boolean isDiscount) {
        this.name = name;
        this.price = price;
        this.isDiscount = isDiscount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isDiscount() {
        return isDiscount;
    }

    public void setDiscount(boolean discount) {
        isDiscount = discount;
    }
}
