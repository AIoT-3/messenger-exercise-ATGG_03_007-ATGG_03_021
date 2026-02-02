package com.message.mapper.dispatch.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.message.TypeManagement;
import com.message.dto.ErrorDto;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import com.message.mapper.dispatch.DispatchMapper;
import com.message.mapper.dispatch.ResponseMapper;
import com.message.mapper.dispatch.ResponseMapperFactory;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.OffsetDateTime;

@Slf4j
public class DispatchMapperImpl implements DispatchMapper {
    private final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public JsonNode readTree(String json) throws ObjectMappingFailException {
        try {
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("JSON 노드로 치환 중 오류 발생");
            throw new ObjectMappingFailException(e.getMessage());
        }
    }

    @Override
    public HeaderDto.RequestHeader requestHeaderPasser(JsonNode rootNode) throws ObjectMappingFailException {
        JsonNode headerNode = rootNode.path("header");
        String type = headerNode.path("type").asText();
        String sessionId = headerNode.path("sessionId").asText();
        OffsetDateTime timestamp = null;
        try {
            timestamp = mapper.convertValue(headerNode.path("timestamp").asText(), OffsetDateTime.class);
        } catch (IllegalArgumentException e) {
            log.error("[해더 파싱] timestamp 파싱중 오류 발생");
            throw new ObjectMappingFailException(e.getMessage());
        }
        return new HeaderDto.RequestHeader(type, timestamp, sessionId);
    }

    @Override
    public String toResult(Object result) throws ObjectMappingFailException {
        ResponseMapper responseMapper = ResponseMapperFactory.getResponseMapper(result.getClass().getName());
        try {
            return responseMapper.toResponse(result);
        } catch (JsonProcessingException e) {
            log.error("응답 내용 JSON화 중 오류 발생");
            throw new ObjectMappingFailException(e.getMessage());
        }
    }

    @Override
    public String toError(ErrorDto response) throws JsonProcessingException {
        // TODO 수정사항 (재민)
        // messageId 필드 추가했으니 여기서도 System.currentTimeMillis() 추가해야함
        HeaderDto.ResponseHeader responseHeader = new HeaderDto.ResponseHeader(TypeManagement.ERROR, false, OffsetDateTime.now(), System.currentTimeMillis());
        ResponseDto<ErrorDto> errorResponse = new ResponseDto<>(responseHeader, response);
        return mapper.writeValueAsString(errorResponse);
    }
}
