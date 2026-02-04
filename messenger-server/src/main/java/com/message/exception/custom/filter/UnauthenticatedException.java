package com.message.exception.custom.filter;

import com.message.domain.ErrorManagement;
import com.message.exception.custom.InvalidRequestException;

public class UnauthenticatedException extends InvalidRequestException {
    public UnauthenticatedException(String message) {
        super(ErrorManagement.Auth.UNAUTHORIZED, message);
    }
}
