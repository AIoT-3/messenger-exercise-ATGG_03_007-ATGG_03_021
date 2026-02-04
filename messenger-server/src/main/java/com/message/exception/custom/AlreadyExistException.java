package com.message.exception.custom;

public class AlreadyExistException extends RuntimeException {
    private static final int HTTP_STATUS = 409;
    private final String code;
    public AlreadyExistException(String code, String message) {
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
