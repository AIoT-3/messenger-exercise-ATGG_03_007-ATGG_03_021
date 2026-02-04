package com.message.service.auth.impl;

import com.message.domain.SessionManagement;
import com.message.domain.UserManagement;
import com.message.dto.data.impl.AuthDto;
import com.message.entity.UserEntity;
import com.message.exception.custom.user.LoginInvalidRequestException;
import com.message.exception.custom.user.UserNotFoundException;
import com.message.service.auth.AuthService;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class AuthServiceImpl implements AuthService {

    @Override
    public UserEntity login(AuthDto.LoginRequest request) {
        if (request.userId().isBlank() || request.password().isBlank()) {
            log.debug("[로그인 시도] 로그인 정보가 비어있습니다 - userId: {}", request.userId());
            throw new LoginInvalidRequestException("[로그인 시도] 로그인 정보가 비어있습니다.");
        }

        UserEntity user = UserManagement.getUser(request.userId());
        if (Objects.isNull(user)) {
            log.debug("[로그인 시도] 존재하지 않는 유저입니다.");
            throw new UserNotFoundException("[로그인 시도] 로그인 정보가 잘못 되었습니다.");
        }

        if (!user.getPassWord().equals(request.password())){
            log.debug("[로그인 시도] 로그인 정보가 잘못 되었습니다 - userId: {}", request.userId());
            throw new LoginInvalidRequestException("[로그인 시도] 로그인 정보가 잘못 되었습니다.");
        }

        return user;
    }

    @Override
    public void logout(String sessionId) {
        SessionManagement.deleteSession(sessionId);
    }
}
