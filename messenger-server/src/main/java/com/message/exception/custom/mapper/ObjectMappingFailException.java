package com.message.exception.custom.mapper;

import com.message.domain.ErrorManagement;
import com.message.exception.custom.BusinessException;

public class ObjectMappingFailException extends BusinessException {
    public ObjectMappingFailException(String message) {
        super(ErrorManagement.Mapper.FAIL_MAPPING, message, 500);
    }
}
