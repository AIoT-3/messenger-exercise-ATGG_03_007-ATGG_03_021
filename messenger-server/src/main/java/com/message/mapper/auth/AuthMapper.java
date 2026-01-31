package com.message.mapper.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.dto.AuthDto;
import com.message.entity.UserEntity;

public interface AuthMapper {
    AuthDto.LoginRequest toLoginRequest(String request) throws JsonProcessingException;
    AuthDto.LoginResponse toLoginResponse(UserEntity user, String uuid);
}
