package com.budgeteer.api.dto.entry;

import java.math.BigDecimal;

public class ReceiptEntryDto {

    private String name;
    private BigDecimal price;

    public ReceiptEntryDto() {
    }

    public ReceiptEntryDto(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
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
}
