package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.dto.data.impl.AuthDto;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.UserDto;
import com.message.mapper.response.AbstractResponseMapper;

import java.time.OffsetDateTime;

public class LoginResponseMapperImpl extends AbstractResponseMapper {
    @Override
    public String getClassName() {
        return AuthDto.LoginResponse.class.getName();
    }


    @Override
    protected String getErrorMessage() {
        return "[로그인] 서버 문제로 결과가 null입니다.";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof AuthDto.LoginResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        return new ResponseDto(
                createHeader(TypeManagement.Auth.LOGIN_SUCCESS),
                (AuthDto.LoginResponse) o
        );
    }
}