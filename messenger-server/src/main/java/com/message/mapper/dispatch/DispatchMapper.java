package com.message.mapper.dispatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.message.dto.ErrorDto;
import com.message.dto.HeaderDto;
import com.message.exception.custom.mapper.ObjectMappingFailException;

public interface DispatchMapper {
    JsonNode readTree(String json) throws ObjectMappingFailException;
    HeaderDto.RequestHeader requestHeaderPasser(JsonNode rootNode) throws ObjectMappingFailException;
    String toResult(Object result) throws JsonProcessingException;
    String toError(ErrorDto response) throws JsonProcessingException;
}
