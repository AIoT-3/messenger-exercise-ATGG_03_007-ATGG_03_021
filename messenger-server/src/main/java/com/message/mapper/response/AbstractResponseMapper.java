package com.message.mapper.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.message.domain.AtomicLongIdManagement;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.exception.custom.mapper.ObjectMappingFailException;

import java.time.OffsetDateTime;
import java.util.Objects;

public abstract class AbstractResponseMapper implements ResponseMapper {
    protected final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);

    public String toResponse(Object result) throws JsonProcessingException {
        if (Objects.isNull(result)) {
            throw new ObjectMappingFailException(getErrorMessage());
        }

        if (isInstanceof(result)) {
            ResponseDto successResponse = createResponseDto(result);
            return mapper.writeValueAsString(successResponse);
        }

        throw new ObjectMappingFailException("[매핑 실패] 리스폰스 매퍼에 잘못된 객체가 전달되었습니다.");
    }

    protected abstract String getErrorMessage();

    protected abstract boolean isInstanceof(Object o);

    protected abstract ResponseDto createResponseDto(Object o);

    protected HeaderDto.ResponseHeader createHeader(String type) {
        return new HeaderDto.ResponseHeader(
                type,
                true,
                OffsetDateTime.now(),
                AtomicLongIdManagement.getResponseMessageIdSequenceIncreateAndGet()
        );
    }
}
