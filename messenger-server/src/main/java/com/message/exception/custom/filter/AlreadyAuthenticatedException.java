package com.message.exception.custom.filter;

import com.message.domain.ErrorManagement;
import com.message.exception.custom.InvalidRequestException;

public class AlreadyAuthenticatedException extends InvalidRequestException {
    public AlreadyAuthenticatedException(String message) {
        super(ErrorManagement.Session.ALREADY_EXISTS, message);
    }
}
