package com.budgeteer.api.dto.receipts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReceiptParseResponse {

    @JsonProperty("shop")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String shopTitle;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("date")
    private String date;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ReceiptParseEntry> entries;

    public ReceiptParseResponse() {

    }

    public ReceiptParseResponse(List<ReceiptParseEntry> entries) {
        this.entries = entries;
    }

    public String getShopTitle() {
        return shopTitle;
    }

    public void setShopTitle(String shopTitle) {
        this.shopTitle = shopTitle;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ReceiptParseEntry> getEntries() {
        if (entries == null) {
            entries = new ArrayList<>();
            return entries;
        }
        return entries;
    }

    public void setEntries(List<ReceiptParseEntry> entries) {
        this.entries = entries;
    }
}
