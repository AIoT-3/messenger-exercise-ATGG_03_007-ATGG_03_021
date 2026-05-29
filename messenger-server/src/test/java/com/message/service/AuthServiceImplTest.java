package com.message.service;

import com.message.dto.data.impl.AuthDto;
import com.message.entity.UserEntity;
import com.message.exception.custom.user.LoginInvalidRequestException;
import com.message.exception.custom.user.UserNotFoundException;
import com.message.service.auth.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthServiceImpl 단위 테스트
 * 기본 등록된 유저: admin/1234, marco/nhnacademy123, jaemin/1234
 */
class AuthServiceImplTest {

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl();
    }

    @Test
    @DisplayName("정상 로그인 - 등록된 유저 & 올바른 비밀번호")
    void login_success() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("admin", "1234");

        UserEntity result = authService.login(request);

        assertNotNull(result);
        assertEquals("admin", result.getUserId());
    }

    @Test
    @DisplayName("정상 로그인 - marco 유저")
    void login_success_marco() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("marco", "nhnacademy123");

        UserEntity result = authService.login(request);

        assertNotNull(result);
        assertEquals("marco", result.getUserId());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 유저")
    void login_userNotFound() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("ghost_user", "pass");

        assertThrows(UserNotFoundException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_wrongPassword() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("admin", "wrong_password");

        assertThrows(LoginInvalidRequestException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("로그인 실패 - userId 공백")
    void login_blankUserId() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("", "1234");

        assertThrows(LoginInvalidRequestException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 공백")
    void login_blankPassword() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("admin", "");

        assertThrows(LoginInvalidRequestException.class, () -> authService.login(request));
    }
}
