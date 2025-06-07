package com.issa.poc.msg;

import com.issa.poc.model.Account;

public class TransSuccessResMsg extends BaseResMsg {
    private String accountNumber;
    private Double balance;

    public TransSuccessResMsg(String description, Account account) {
        this.setStatusCode("000");
        this.setDescription(description);
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
