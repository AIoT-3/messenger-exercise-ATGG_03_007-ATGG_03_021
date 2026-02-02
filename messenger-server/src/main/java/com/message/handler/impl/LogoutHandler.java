package com.message.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.TypeManagement;
import com.message.dto.AuthDto;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import com.message.handler.Handler;
import com.message.mapper.auth.AuthMapper;
import com.message.mapper.auth.impl.AuthMapperImpl;
import com.message.service.auth.AuthService;
import com.message.service.auth.impl.AuthImplService;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

// TODO 수정사항 (재민)
// LogoutHandler 생성

@Slf4j
public class LogoutHandler implements Handler {

    private final AuthService authService = new AuthImplService();
    private final AuthMapper authMapper = new AuthMapperImpl();

    @Override
    public String getMethod() {
        return TypeManagement.Auth.LOGOUT;
    }

    @Override
    public Object execute(String value) {

        // 요청 매핑 (헤더에서 sessionId 추출)
        String sessionId;
        try{
            sessionId = authMapper.toSessionId(value);
        } catch (JsonProcessingException e) {
            log.error("[로그아웃 요청] 매핑 실패 - message: {]", e.getMessage());
            throw new ObjectMappingFailException("[로그아웃 요쳥] 매핑 실패");
        }

        // 서비스 로직 실행 (세션 삭제)
        authService.logout(sessionId);

        // 매퍼를 통해 성공 데이터 dto 생성
        AuthDto.LogoutResponse response = authMapper.toLogoutResponse();
        log.debug("[로그아웃 시도] 성공 - sessionId: {}", sessionId);

        return response;
    }
}
