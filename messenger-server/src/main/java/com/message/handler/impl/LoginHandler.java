package com.message.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.domain.SessionManagement;
import com.message.dto.AuthDto;
import com.message.entity.UserEntity;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import com.message.handler.Handler;
import com.message.mapper.auth.AuthMapper;
import com.message.mapper.auth.impl.AuthMapperImpl;
import com.message.service.auth.AuthService;
import com.message.service.auth.impl.AuthImplService;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class LoginHandler implements Handler {
    private static final String METHOD = "LOGIN";

    private final AuthMapper authMapper = new AuthMapperImpl();
    private final AuthService authService = new AuthImplService();


    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    public Object execute(String value) {

        // 요청 매핑
        AuthDto.LoginRequest request;
        try {
            request = authMapper.toLoginRequest(value);
        } catch (JsonProcessingException e) {
            log.error("[로그인 요청] 매핑 실패 - message: {}", e.getMessage());
            throw new ObjectMappingFailException("[로그인 요청] 매핑 실패");
        }

        // 서비스 로직 실행 (유저 검증)
        UserEntity user = authService.login(request);

        // TODO 수정사항 (재민)
        // 서버에 로그인 상태를 유지(세션 관리)하는 단계 빠져있음 -> SessionManagement에 저장(세션 저장)
        UUID uuid = UUID.randomUUID();
        String sessionId = uuid.toString();

        // 이후 요청에서 이 sessionId를 보고 유저 식별
        SessionManagement.addSessions(sessionId, user.getUserId());

        AuthDto.LoginResponse response = authMapper.toLoginResponse(user, sessionId);
        log.debug("[로그인 시도] 로그인 성공 - userId: {}, sessionId: {}", response.userId(), sessionId);

        return response;
    }
}
