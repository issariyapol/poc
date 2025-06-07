package com.issa.poc.model;

import java.util.ArrayList;

public class Statement {
    private double balance;
    private String valueAsOf;
    private ArrayList<StatementRecord> statementRecords;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getValueAsOf() {
        return valueAsOf;
    }

    public void setValueAsOf(String valueAsOf) {
        this.valueAsOf = valueAsOf;
    }

    public ArrayList<StatementRecord> getStatementRecords() {
        return statementRecords;
    }

    public void setStatementRecords(ArrayList<StatementRecord> statementRecords) {
        this.statementRecords = statementRecords;
    }
}
