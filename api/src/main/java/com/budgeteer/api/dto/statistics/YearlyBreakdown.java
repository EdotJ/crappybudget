package com.budgeteer.api.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class YearlyBreakdown {

    @JsonProperty("breakdown")
    private List<Entry> entries;

    public YearlyBreakdown() {
    }

    public YearlyBreakdown(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public static class Entry {
        private int month;
        private BigDecimal income;
        private BigDecimal expenses;

        public Entry() {
        }

        public Entry(int month, BigDecimal income, BigDecimal expenses) {
            this.month = month;
            this.income = income;
            this.expenses = expenses;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public BigDecimal getIncome() {
            return income;
        }

        public void setIncome(BigDecimal income) {
            this.income = income;
        }

        public BigDecimal getExpenses() {
            return expenses;
        }

        public void setExpenses(BigDecimal expenses) {
            this.expenses = expenses;
        }
    }
}
