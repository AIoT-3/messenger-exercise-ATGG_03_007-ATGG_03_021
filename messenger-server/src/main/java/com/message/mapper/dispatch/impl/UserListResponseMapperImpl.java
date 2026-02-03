package com.message.mapper.dispatch.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.UserDto;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import com.message.mapper.dispatch.ResponseMapper;

import java.time.OffsetDateTime;

// TODO 수정사항 (재민)
public class UserListResponseMapperImpl implements ResponseMapper {
    private final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public String getClassName() {
        return UserDto.UserListResponse.class.getName();
    }

    @Override
    public String toResponse(Object result) throws JsonProcessingException {
        if(result instanceof UserDto.UserListResponse userListResponse) {
            HeaderDto.ResponseHeader responseHeader = new HeaderDto.ResponseHeader(
                    TypeManagement.User.LIST_SUCCESS,
                    true,
                    OffsetDateTime.now(),
                    AtomicLongIdManagement.getMessageIdSequenceIncreateAndGet()
            );

            return mapper.writeValueAsString(new ResponseDto(responseHeader, userListResponse));
        }

        throw new ObjectMappingFailException("[매핑 실패] 유저리스트 매퍼에 잘못된 객체 전달됨");
    }
}
