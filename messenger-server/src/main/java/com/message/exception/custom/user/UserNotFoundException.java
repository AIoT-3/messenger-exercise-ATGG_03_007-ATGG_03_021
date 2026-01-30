package com.message.exception.custom.user;

import com.message.domain.ErrorManagement;
import com.message.exception.custom.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(ErrorManagement.Auth.INVALID_CREDENTIALS, message);
    }
}
