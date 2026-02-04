package com.message.exception.custom.user;

import com.message.domain.ErrorManagement;
import com.message.exception.custom.InvalidRequestException;

public class LoginInvalidRequestException extends InvalidRequestException {
    public LoginInvalidRequestException(String message) {
        super(ErrorManagement.Auth.INVALID_CREDENTIALS, message);
    }
}
