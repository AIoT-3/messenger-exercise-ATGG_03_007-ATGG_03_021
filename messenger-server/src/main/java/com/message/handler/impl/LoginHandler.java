package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.domain.SessionManagement;
import com.message.domain.SocketManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.AuthDto;
import com.message.entity.UserEntity;
import com.message.handler.Handler;
import com.message.mapper.auth.AuthMapper;
import com.message.mapper.auth.impl.AuthMapperImpl;
import com.message.mapper.sync.impl.UserSyncResponseMapper;
import com.message.service.auth.AuthService;
import com.message.service.auth.impl.AuthServiceImpl;
import com.message.service.user.UserService;
import com.message.service.user.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public class LoginHandler implements Handler {

    private final AuthMapper authMapper;
    private final AuthService authService;
    private final UserService userService;
    private final UserSyncResponseMapper userSyncResponseMapper;
    private final SessionManagement sessionManagement;
    private final SocketManagement socketManagement;

    public LoginHandler() {
        this(
            new AuthMapperImpl(),
            new AuthServiceImpl(),
            new UserServiceImpl(),
            new UserSyncResponseMapper(),
            SessionManagement.getInstance(),
            SocketManagement.getInstance()
        );
    }

    LoginHandler(AuthMapper authMapper, AuthService authService, UserService userService,
                 UserSyncResponseMapper userSyncResponseMapper,
                 SessionManagement sessionManagement, SocketManagement socketManagement) {
        this.authMapper = authMapper;
        this.authService = authService;
        this.userService = userService;
        this.userSyncResponseMapper = userSyncResponseMapper;
        this.sessionManagement = sessionManagement;
        this.socketManagement = socketManagement;
    }

    @Override
    public String getMethod() {
        return TypeManagement.Auth.LOGIN;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        AuthDto.LoginRequest request = (AuthDto.LoginRequest) data;

        UserEntity user = authService.login(request);

        List<String> allSessionIds = sessionManagement.getAllSessionIds();

        String sessionId = UUID.randomUUID().toString();
        sessionManagement.addSessions(sessionId, user.getUserId());

        AuthDto.LoginResponse response = authMapper.toLoginResponse(user, sessionId);
        log.debug("[로그인 시도] 로그인 성공 - userId: {}, sessionId: {}", response.userId(), sessionId);

        sendSynchronizedUsers(allSessionIds);

        return response;
    }

    private void sendSynchronizedUsers(List<String> sessionIds) {
        if (sessionIds.isEmpty()) {
            log.debug("동기화할 유저가 없습니다.");
            return;
        }
        log.debug("[로그인 성공] 유저 목록 새로고침 시작");
        String loginSuccessMessage = userSyncResponseMapper.toSyncResponse(userService.getUserList());
        log.debug("[로그인 성공] 모든 접속중인 유저 동기화 시도");
        socketManagement.sendSynchronizedMessage(sessionIds, loginSuccessMessage);
    }
}
