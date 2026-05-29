package com.message.exception;

import com.message.domain.ErrorManagement;
import com.message.dto.data.impl.ErrorDto;
import com.message.exception.custom.BusinessException;
import com.message.exception.custom.InvalidRequestException;
import com.message.exception.custom.NotFoundException;
import com.message.exception.custom.filter.AlreadyAuthenticatedException;
import com.message.exception.custom.filter.UnauthenticatedException;
import com.message.exception.custom.handler.HandlerNotFoundException;
import com.message.exception.custom.user.LoginInvalidRequestException;
import com.message.exception.custom.user.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("LoginInvalidRequestException 처리")
    void handle_loginInvalidRequest() {
        ErrorDto dto = handler.exceptionHandler(new LoginInvalidRequestException("잘못된 로그인"));

        assertEquals(ErrorManagement.Auth.INVALID_CREDENTIALS, dto.code());
        assertNotNull(dto.message());
    }

    @Test
    @DisplayName("UserNotFoundException 처리 - AUTH.INVALID_CREDENTIALS 코드 사용")
    void handle_userNotFound() {
        ErrorDto dto = handler.exceptionHandler(new UserNotFoundException("유저 없음"));

        // UserNotFoundException은 보안상 AUTH.INVALID_CREDENTIALS 코드를 사용 (유저 존재 여부 노출 방지)
        assertEquals(ErrorManagement.Auth.INVALID_CREDENTIALS, dto.code());
    }

    @Test
    @DisplayName("AlreadyAuthenticatedException 처리")
    void handle_alreadyAuthenticated() {
        ErrorDto dto = handler.exceptionHandler(new AlreadyAuthenticatedException("이미 로그인"));

        assertNotNull(dto.code());
        assertNotNull(dto.message());
    }

    @Test
    @DisplayName("UnauthenticatedException 처리")
    void handle_unauthenticated() {
        ErrorDto dto = handler.exceptionHandler(new UnauthenticatedException("미인증"));

        assertNotNull(dto.code());
    }

    @Test
    @DisplayName("HandlerNotFoundException 처리")
    void handle_handlerNotFound() {
        ErrorDto dto = handler.exceptionHandler(new HandlerNotFoundException("핸들러 없음"));

        assertEquals(ErrorManagement.Handler.NOT_FOUND, dto.code());
    }

    @Test
    @DisplayName("BusinessException 처리")
    void handle_businessException() {
        ErrorDto dto = handler.exceptionHandler(
            new BusinessException("CUSTOM.CODE", "커스텀 에러", 500)
        );

        assertEquals("CUSTOM.CODE", dto.code());
        assertEquals("커스텀 에러", dto.message());
    }

    @Test
    @DisplayName("알 수 없는 예외 처리 - INTERNAL_ERROR 코드 반환")
    void handle_unknownException() {
        ErrorDto dto = handler.exceptionHandler(new RuntimeException("예상치 못한 오류"));

        assertEquals("INTERNAL_ERROR", dto.code());
    }
}
