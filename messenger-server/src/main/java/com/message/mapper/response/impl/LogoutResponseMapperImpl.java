package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.AuthDto;
import com.message.dto.data.impl.UserDto;
import com.message.mapper.response.AbstractResponseMapper;

import java.time.OffsetDateTime;

public class LogoutResponseMapperImpl extends AbstractResponseMapper {
    @Override
    public String getClassName() {
        // Factory에서 찾을 수 있도록 LogoutResponse의 풀네임 반환
        return AuthDto.LogoutResponse.class.getName();
    }

    @Override
    protected String getErrorMessage() {
        return "[로그아웃] 서버 문제로 결과가 null입니다.";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof AuthDto.LogoutResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        return new ResponseDto(
                createHeader(TypeManagement.Auth.LOGOUT_SUCCESS),
                (AuthDto.LogoutResponse) o
        );
    }
}
