package com.message.mapper.response;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ResponseMapper {
    String getClassName();
    String toResponse(Object result) throws JsonProcessingException;
}
