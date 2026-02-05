package com.message.mapper.dispatch.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.domain.ErrorManagement;
import com.message.dto.RequestDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.ErrorDto;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.exception.custom.BusinessException;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import com.message.mapper.dispatch.DispatchMapper;
import com.message.mapper.response.ResponseMapper;
import com.message.mapper.response.ResponseMapperFactory;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.OffsetDateTime;
import java.util.Objects;

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
    public HeaderDto.RequestHeader requestHeaderParser(JsonNode rootNode) throws ObjectMappingFailException {
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
    public RequestDataDto requestDataParser(String type, JsonNode rootNode) throws ObjectMappingFailException {
        Class<? extends RequestDataDto> clazz = TypeManagement.requestDataDtoClassMap.get(type);
        if (Objects.isNull(clazz)) {
            return null;
        }
        log.debug("[data 파싱] 파싱할 클래스 확인 - {}", clazz.getName());

        try {
            JsonNode dataNode = rootNode.path("data");
            log.debug("data 내용: {}", dataNode.toString()); // 로그 볼 때 객체 내용 다 보려고

            if (dataNode.isMissingNode() || dataNode.isNull()) {
                log.warn("[data 파싱] data 노드가 비어있습니다.");
                throw new ObjectMappingFailException("[data 파싱] data 노드가 비어있습니다.");
            }

            return mapper.readValue(dataNode.traverse(), clazz);
        } catch (Exception e) {
            log.error("[data 파싱] 파싱 오류 발생");
            throw new ObjectMappingFailException(e.getMessage());
        }
    }

    @Override
    public RequestDto requestParser(HeaderDto.RequestHeader header, RequestDataDto data) throws ObjectMappingFailException {
        if (Objects.isNull(header)) {
            log.error("[요청 파싱] 헤더가 비어있습니다");
            throw new BusinessException(ErrorManagement.Request.IS_NULL, "헤더 빔", 400);
        }

        if (TypeManagement.requestDataDtoClassMap.containsKey(header.type()) && Objects.isNull(data)) {
            log.error("[요청 파싱] 데이터가 비어있습니다.");
            throw new BusinessException(ErrorManagement.Request.IS_NULL, "데이터 빔", 400);
        }

        return new RequestDto(header, data);
    }

    @Override
    public String toResult(Object result) throws ObjectMappingFailException {
        log.debug("[매퍼 조회 시도] 클래스 이름: {}", result.getClass().getName());
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
        HeaderDto.ResponseHeader responseHeader = new HeaderDto.ResponseHeader(
                TypeManagement.ERROR,
                false,
                OffsetDateTime.now(),
                AtomicLongIdManagement.getResponseMessageIdSequenceIncreateAndGet()
        );
        ResponseDto errorResponse = new ResponseDto(responseHeader, response);
        return mapper.writeValueAsString(errorResponse);
    }
}
