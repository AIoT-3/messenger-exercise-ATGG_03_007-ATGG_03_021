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

    private final AuthMapper authMapper;
    private final UserService userService;
    private final UserSyncResponseMapper userSyncResponseMapper;
    private final SessionManagement sessionManagement;
    private final SocketManagement socketManagement;

    public LogoutHandler() {
        this(
            new AuthMapperImpl(),
            new UserServiceImpl(),
            new UserSyncResponseMapper(),
            SessionManagement.getInstance(),
            SocketManagement.getInstance()
        );
    }

    LogoutHandler(AuthMapper authMapper, UserService userService, UserSyncResponseMapper userSyncResponseMapper,
                  SessionManagement sessionManagement, SocketManagement socketManagement) {
        this.authMapper = authMapper;
        this.userService = userService;
        this.userSyncResponseMapper = userSyncResponseMapper;
        this.sessionManagement = sessionManagement;
        this.socketManagement = socketManagement;
    }

    @Override
    public String getMethod() {
        return TypeManagement.Auth.LOGOUT;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        String sessionId = header.sessionId();
        sessionManagement.deleteSession(sessionId);

        AuthDto.LogoutResponse response = authMapper.toLogoutResponse();
        log.debug("[로그아웃 시도] 성공 - sessionId: {}", sessionId);

        sendSynchronizedUsers(sessionManagement.getAllSessionIds());

        return response;
    }

    private void sendSynchronizedUsers(List<String> sessionIds) {
        if (sessionIds.isEmpty()) {
            log.debug("동기화할 유저가 없습니다.");
            return;
        }
        String message = userSyncResponseMapper.toSyncResponse(userService.getUserList());
        socketManagement.sendSynchronizedMessage(sessionIds, message);
    }
}
