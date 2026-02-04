package com.message.mapper.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.dto.data.impl.AuthDto;
import com.message.entity.UserEntity;

public interface AuthMapper {
    // 로그인
    AuthDto.LoginRequest toLoginRequest(String request) throws JsonProcessingException;
    AuthDto.LoginResponse toLoginResponse(UserEntity user, String uuid);

    // 로그아웃 성공 데이터 생성
    AuthDto.LogoutResponse toLogoutResponse();
}
