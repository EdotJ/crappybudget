package com.budgeteer.api.dto.receipts;

import java.math.BigDecimal;

public class ReceiptParseEntry {

    private String label;

    private BigDecimal price;

    private boolean isDiscount;

    public ReceiptParseEntry() {

    }

    public ReceiptParseEntry(String label, BigDecimal price) {
        this(label, price, false);
    }

    public ReceiptParseEntry(String label, BigDecimal price, boolean isDiscount) {
        this.label = label;
        this.price = price;
        this.isDiscount = isDiscount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
