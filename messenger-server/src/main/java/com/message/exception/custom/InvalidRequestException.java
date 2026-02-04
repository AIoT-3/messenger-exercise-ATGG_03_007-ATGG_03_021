package com.message.exception.custom;

public class InvalidRequestException extends RuntimeException {
    private static final int HTTP_STATUS = 400;
    private final String code;
    public InvalidRequestException(String code, String message) {
        super(message);
        this.code = code;
    }
    public int getHttpStatus() {
        return HTTP_STATUS;
    }
    public String getCode(){
        return code;
    }
}