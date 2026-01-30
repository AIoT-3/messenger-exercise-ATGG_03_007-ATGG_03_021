package com.message.mapper.dispatch.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.message.dto.ErrorResponse;
import com.message.mapper.dispatch.DispatchMapper;

public class DispatchMapperImpl implements DispatchMapper {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public JsonNode readTree(String json) throws JsonProcessingException {
        return mapper.readTree(json);
    }

    @Override
    public String toResult(Object result) {
        return "";
    }

    @Override
    public String toError(ErrorResponse response) {
        return "";
    }
}
