package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.domain.SessionManagement;
import com.message.domain.SocketManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.AuthDto;
import com.message.handler.Handler;
import com.message.mapper.auth.AuthMapper;
import com.message.mapper.auth.impl.AuthMapperImpl;
import com.message.mapper.sync.impl.UserSyncResponseMapper;
import com.message.service.user.UserService;
import com.message.service.user.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LogoutHandler implements Handler {

    private final AuthMapper authMapper = new AuthMapperImpl();

    private final UserService userService = new UserServiceImpl();
    private final UserSyncResponseMapper userSyncResponseMapper = new UserSyncResponseMapper();

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

        sendSynchronizedUsers(SessionManagement.getAllSessionIds());

        return response;
    }

    private void sendSynchronizedUsers(List<String> sessionIds) {
        String loginSuccessMessage = userSyncResponseMapper.toSyncResponse(userService.getUserList());
        SocketManagement.sendSynchronizedMessage(sessionIds, loginSuccessMessage);
    }
}
