package com.message.mapper.dispatch.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.dto.data.impl.AuthDto;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import com.message.mapper.dispatch.ResponseMapper;

import java.time.OffsetDateTime;
import java.util.Objects;

public class LogoutResponseMapperImpl implements ResponseMapper {
    private final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // 재민님의 취향 반영!
    }

    @Override
    public String getClassName() {
        // Factory에서 찾을 수 있도록 LogoutResponse의 풀네임 반환
        return AuthDto.LogoutResponse.class.getName();
    }

    @Override
    public String toResponse(Object result) throws JsonProcessingException {
        if(Objects.isNull(result)){
            throw new ObjectMappingFailException("[로그아웃] 서버 문제로 결과가 null입니다.");
        }

        if(result instanceof AuthDto.LogoutResponse logoutResponse){
            // 헤더 설정 (LOGOUT_SUCCESS 타입, 성공여부 true, 현재시간, 메시지ID)
            HeaderDto.ResponseHeader responseHeader = new HeaderDto.ResponseHeader(
                    TypeManagement.Auth.LOGOUT_SUCCESS,
                    true,
                    OffsetDateTime.now(),
                    AtomicLongIdManagement.getMessageIdSequenceIncreateAndGet()
            );

            ResponseDto successResponse = new ResponseDto(responseHeader, logoutResponse);
            return mapper.writeValueAsString(successResponse);
        }

        throw new ObjectMappingFailException("[매핑 실패] 로그아웃 리스폰스 매퍼에 잘못된 객체가 전달되었습니다.");
    }
}
