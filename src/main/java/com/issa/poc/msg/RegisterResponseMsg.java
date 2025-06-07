package com.issa.poc.msg;

public class RegisterResponseMsg {
    private String statusCode;
    private String description;

    public RegisterResponseMsg(String statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public RegisterResponseMsg() {

    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
