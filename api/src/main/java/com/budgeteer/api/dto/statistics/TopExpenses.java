package com.budgeteer.api.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TopExpenses {

    @JsonProperty("expenses")
    private List<Entry> entries;

    public TopExpenses() {
    }

    public TopExpenses(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public static class Entry {
        private String name;
        private String account;
        private BigDecimal value;
        private LocalDate date;

        public Entry() {
        }

        public Entry(String name, String account, BigDecimal value, LocalDate date) {
            this.name = name;
            this.account = account;
            this.value = value;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }
    }
}
