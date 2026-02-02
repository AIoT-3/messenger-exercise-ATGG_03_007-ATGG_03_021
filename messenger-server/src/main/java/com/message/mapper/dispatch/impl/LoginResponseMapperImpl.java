package com.message.mapper.dispatch.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.dto.data.impl.AuthDto;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import com.message.mapper.dispatch.ResponseMapper;

import java.time.OffsetDateTime;
import java.util.Objects;

public class LoginResponseMapperImpl implements ResponseMapper {
    private final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // 터미널에서 걍 제이슨 한 줄로 보기 싫어서
    }

    @Override
    public String getClassName() {
        return AuthDto.LoginResponse.class.getName();
    }

    @Override
    public String toResponse(Object result) throws JsonProcessingException {
        if(Objects.isNull(result)){
            throw new ObjectMappingFailException("[로그인] 서버 문제로 결과가 null입니다.");
        }

        if(result instanceof AuthDto.LoginResponse loginResponse){
            HeaderDto.ResponseHeader responseHeader = new HeaderDto.ResponseHeader(
                    TypeManagement.Auth.LOGIN_SUCCESS,
                    true,
                    OffsetDateTime.now(),
                    AtomicLongIdManagement.getMessageIdSequenceIncreateAndGet()
            );
            ResponseDto successResponse = new ResponseDto(responseHeader, loginResponse);
            return mapper.writeValueAsString(successResponse);
        }
        throw new ObjectMappingFailException("[매핑 실패] 해당하는 매퍼가 호출되었습니다");
    }
}
