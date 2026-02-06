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

import java.net.Socket;
import java.util.List;
import java.util.UUID;

@Slf4j
public class LoginHandler implements Handler {

    private final AuthMapper authMapper = new AuthMapperImpl();
    private final AuthService authService = new AuthServiceImpl();
    private final UserService userService = new UserServiceImpl();
    private final UserSyncResponseMapper userSyncResponseMapper = new UserSyncResponseMapper();

    @Override
    public String getMethod() {
        return TypeManagement.Auth.LOGIN;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {

        // 요청 매핑
        AuthDto.LoginRequest request = (AuthDto.LoginRequest) data;

        // 서비스 로직 실행 (유저 검증)
        UserEntity user = authService.login(request);

        List<String> allSessionIds = SessionManagement.getAllSessionIds();

        UUID uuid = UUID.randomUUID();
        String sessionId = uuid.toString();

        // 이후 요청에서 이 sessionId를 보고 유저 식별
        SessionManagement.addSessions(sessionId, user.getUserId());

        AuthDto.LoginResponse response = authMapper.toLoginResponse(user, sessionId);
        log.debug("[로그인 시도] 로그인 성공 - userId: {}, sessionId: {}", response.userId(), sessionId);

        sendSynchronizedUsers(allSessionIds);

        return response;
    }

    private void sendSynchronizedUsers(List<String> sessionIds) {
        if(sessionIds.isEmpty()){
            log.debug("동기화할 유저가 없습니다.");
            return;
        }
        log.debug("[로그인 성공] 유저 목록 새로고침 시작");
        String loginSuccessMessage = userSyncResponseMapper.toSyncResponse(userService.getUserList());
        log.debug("[로그인 성공] 모든 접속중인 유저 동기화 시도");
        SocketManagement.sendSynchronizedMessage(sessionIds, loginSuccessMessage);
    }
}
