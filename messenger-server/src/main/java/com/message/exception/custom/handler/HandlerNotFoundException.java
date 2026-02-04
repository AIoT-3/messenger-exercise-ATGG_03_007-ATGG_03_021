package com.message.exception.custom.handler;

import com.message.domain.ErrorManagement;
import com.message.exception.custom.BusinessException;
import com.message.exception.custom.NotFoundException;

public class HandlerNotFoundException extends NotFoundException {
    public HandlerNotFoundException(String message) {
        super(ErrorManagement.Handler.NOT_FOUND, message);
    }
}

