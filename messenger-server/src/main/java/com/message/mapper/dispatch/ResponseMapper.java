package com.message.mapper.dispatch;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ResponseMapper {
    String getClassName();
    String toResponse(Object result) throws JsonProcessingException;
}
