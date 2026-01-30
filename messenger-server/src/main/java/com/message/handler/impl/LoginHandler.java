package com.message.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final String METHOD = "login";

    private final AuthMapper authMapper = new AuthMapperImpl();
    private final AuthService authService = new AuthImplService();


    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    public String execute(String value) {

        AuthDto.LoginRequest request;
        try {
            request = authMapper.toLoginRequest(value);
        } catch (JsonProcessingException e) {
            log.error("[로그인 요청] 매핑 실패 - message: {}", e.getMessage());
            throw new ObjectMappingFailException("[로그인 요청] 매핑 실패");
        }

        UserEntity user = authService.login(request);

        UUID uuid = UUID.randomUUID();

        AuthDto.LoginResponse response = authMapper.toLoginResponse(user, uuid.toString());

        String responseJson;
        try {
            responseJson = authMapper.toJson(response);
        } catch (JsonProcessingException e) {
            throw new ObjectMappingFailException("[로그인 요청] 로그인 응답 json 변환 실패");
        }

        SessionManagement.addSessions(user.getUserId(), uuid.toString());

        return responseJson;
    }
}
