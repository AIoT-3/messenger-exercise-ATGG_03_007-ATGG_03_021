package com.message.mapper.dispatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.message.dto.ErrorResponse;

public interface DispatchMapper {
    JsonNode readTree(String json) throws JsonProcessingException;
    String toResult(Object result);
    String toError(ErrorResponse response);
}
