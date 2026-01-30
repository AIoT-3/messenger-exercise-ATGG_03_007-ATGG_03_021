package com.message.exception.custom;

public class NotFoundException extends RuntimeException {
    private static final int HTTP_STATUS = 404;
    private final String code;
    public NotFoundException(String code, String message) {
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