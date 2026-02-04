package com.message.mapper.dispatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.message.dto.RequestDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.ErrorDto;
import com.message.dto.HeaderDto;
import com.message.exception.custom.mapper.ObjectMappingFailException;

public interface DispatchMapper {
    JsonNode readTree(String json) throws ObjectMappingFailException;
    HeaderDto.RequestHeader requestHeaderParser(JsonNode rootNode) throws ObjectMappingFailException;
    RequestDataDto requestDataParser(String type, JsonNode rootNode) throws ObjectMappingFailException;
    RequestDto requestParser(HeaderDto.RequestHeader header, RequestDataDto data) throws ObjectMappingFailException;
    String toResult(Object result) throws JsonProcessingException;
    String toError(ErrorDto response) throws JsonProcessingException;
}
