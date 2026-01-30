package com.message.mapper.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.message.dto.AuthDto;
import com.message.entity.UserEntity;
import com.message.exception.custom.user.LoginInvalidRequestException;
import com.message.mapper.auth.AuthMapper;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

public class AuthMapperImpl implements AuthMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AuthDto.LoginRequest toLoginRequest(String request) throws JsonProcessingException {
        if(Objects.isNull(request) || request.isBlank()){
            throw new LoginInvalidRequestException("Invalid username or password");
        }

        return objectMapper.readValue(request, AuthDto.LoginRequest.class);
    }

    @Override
    public AuthDto.LoginResponse toLoginResponse(UserEntity user, String uuid) {
        return new AuthDto.LoginResponse(user.getUserId(), uuid, "Welcome!");
    }

    @Override
    public String toJson(AuthDto.LoginResponse response) throws JsonProcessingException {
        return objectMapper.writeValueAsString(response);
    }
}
