package com.issa.poc.msg;

import com.issa.poc.exception.AccountNotFoundException;

public class TransErrResMsg extends BaseResMsg {
    public TransErrResMsg(AccountNotFoundException e) {
        this.setStatusCode(AccountNotFoundException.STATUS_CODE);
        this.setDescription(AccountNotFoundException.DESCRIPTION);
    }

    public TransErrResMsg(String number, String message) {
        this.setStatusCode(number);
        this.setDescription(message);
    }
}
