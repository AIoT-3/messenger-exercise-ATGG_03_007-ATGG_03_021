package com.message.mapper.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.message.dto.data.impl.AuthDto;
import com.message.entity.UserEntity;
import com.message.exception.custom.user.LoginInvalidRequestException;
import com.message.mapper.auth.AuthMapper;

import java.util.Objects;

public class AuthMapperImpl implements AuthMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AuthDto.LoginRequest toLoginRequest(String request) throws JsonProcessingException {
        if (Objects.isNull(request) || request.isBlank()) {
            throw new LoginInvalidRequestException("Invalid username or password");
        }

        // 전체 제이슨을 트리 구조로 읽음
        JsonNode rootNode = objectMapper.readTree(request);

        // data 노드만 추출
        JsonNode dataNode = rootNode.path("data");

        return objectMapper.treeToValue(dataNode, AuthDto.LoginRequest.class);
    }

    @Override
    public AuthDto.LoginResponse toLoginResponse(UserEntity user, String uuid) {
        return new AuthDto.LoginResponse(user.getUserId(), uuid, "Welcome!");
    }

    @Override
    public String toSessionId(String request) throws JsonProcessingException {
        if (Objects.isNull(request) || request.isBlank()) {
            throw new LoginInvalidRequestException("요청 본문이 비어있습니다.");
        }

        // 제이슨의 헤더 -> sessionId 경로 따라가서 값 가져옴
        JsonNode rootNode = objectMapper.readTree(request);
        return rootNode.path("header").path("sessionId").asText();
    }

    @Override
    public AuthDto.LogoutResponse toLogoutResponse() {
        // "message" : "로그아웃 되었습니다."
        return new AuthDto.LogoutResponse("로그아웃 되었습니다.");
    }
}

