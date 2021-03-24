package com.budgeteer.api.dto.account;

import java.math.BigDecimal;

public class AccountBalanceDto {

    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal net;

    public AccountBalanceDto() {

    }

    public AccountBalanceDto(BigDecimal income, BigDecimal expenses) {
        this.income = income;
        this.expenses = expenses;
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

    public BigDecimal getNet() {
        return income.subtract(expenses);
    }

    public void setNet(BigDecimal net) {
        this.net = net;
    }

}
