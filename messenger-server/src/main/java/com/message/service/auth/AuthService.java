package com.message.service.auth;

import com.message.dto.AuthDto;
import com.message.entity.UserEntity;

public interface AuthService {
    // 성공 시 SessionId 반환, 실패 시 예외 발생
    UserEntity login(AuthDto.LoginRequest request);
    void logout(String sessionId);
}