package com.message.mapper.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.dto.AuthDto;
import com.message.entity.UserEntity;

public interface AuthMapper {
    // 로그인
    AuthDto.LoginRequest toLoginRequest(String request) throws JsonProcessingException;
    AuthDto.LoginResponse toLoginResponse(UserEntity user, String uuid);

    // TODO 수정사항 (재민)
    // 로그아웃
    // 요청 제이슨에서 sessionId만 뽑아오기
    String toSessionId(String request) throws JsonProcessingException;

    // 로그아웃 성공 데이터 생성
    AuthDto.LogoutResponse toLogoutResponse();
}
