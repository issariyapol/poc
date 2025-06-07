package com.issa.poc.msg;

public class AcctErrResMsg extends BaseResMsg{
    public AcctErrResMsg(String statusCode, String description) {
        this.setStatusCode(statusCode);
        this.setDescription(description);
    }
}
