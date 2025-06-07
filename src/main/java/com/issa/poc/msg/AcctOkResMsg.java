package com.issa.poc.msg;

import com.issa.poc.model.Account;
import com.issa.poc.exception.CustomerNotFoundException;
import com.issa.poc.exception.AccountInvalidCustomerNameException;

public class AcctOkResMsg extends BaseResMsg {

    private String accountNumber;
    private Double accountBalance;
    private String accountStatus;

    public AcctOkResMsg(String description, Account account) {
        setStatusCode("000");
        setDescription(description);
        this.accountNumber = account.getAccountNumber();
        this.accountBalance = account.getBalance();
        this.accountStatus = account.getStatus();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
