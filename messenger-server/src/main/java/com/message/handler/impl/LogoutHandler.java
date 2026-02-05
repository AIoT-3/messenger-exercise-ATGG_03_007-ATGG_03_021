package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.domain.SessionManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.AuthDto;
import com.message.handler.Handler;
import com.message.mapper.auth.AuthMapper;
import com.message.mapper.auth.impl.AuthMapperImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogoutHandler implements Handler {

    private final AuthMapper authMapper = new AuthMapperImpl();

    @Override
    public String getMethod() {
        return TypeManagement.Auth.LOGOUT;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {

        // 요청 매핑 (헤더에서 sessionId 추출)
        String sessionId = header.sessionId();

        // 세션 삭제
        SessionManagement.deleteSession(sessionId);

        // 매퍼를 통해 성공 데이터 dto 생성
        AuthDto.LogoutResponse response = authMapper.toLogoutResponse();
        log.debug("[로그아웃 시도] 성공 - sessionId: {}", sessionId);

        return response;
    }
}
