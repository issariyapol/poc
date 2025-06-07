package com.issa.poc.msg;

public class RegisterSuccessResMsg extends RegisterResponseMsg {
    public RegisterSuccessResMsg() {
        super();
        setStatusCode("000");
        setDescription("Register Successful");
    }
}
