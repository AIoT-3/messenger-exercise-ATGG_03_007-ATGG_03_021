package com.message.exception.custom.user;

import com.message.domain.ErrorManagement;
import com.message.exception.custom.AlreadyExistException;

public class AlreadyUserExistException extends AlreadyExistException {
    public AlreadyUserExistException(String message) {
        super(ErrorManagement.User.ALREADY_EXISTS, message);
    }
}
