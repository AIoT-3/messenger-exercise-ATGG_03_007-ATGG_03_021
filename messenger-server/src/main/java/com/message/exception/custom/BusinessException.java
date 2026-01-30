package com.message.exception.custom;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int httpStatus;
    private final String code;

    public BusinessException(String code,String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}