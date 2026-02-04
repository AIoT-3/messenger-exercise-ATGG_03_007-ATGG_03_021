package com.message.action.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.message.TypeManagement;
import com.message.action.MessageAction;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.AuthDto;
import com.message.dto.data.impl.ChatDto;
import com.message.dto.data.impl.ErrorDto;
import com.message.dto.data.impl.RoomDto;
import com.message.dto.data.impl.UserDto;
import com.message.subject.EventType;
import com.message.ui.form.MessageClientForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

/**
 * 응답 메시지 Action
 * 서버로부터 받은 응답을 UI에 반영
 */
public class ResponseMessageAction implements MessageAction {
    private static final Logger log = LoggerFactory.getLogger(ResponseMessageAction.class);

    private final MessageClientForm form;
    private final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public ResponseMessageAction(MessageClientForm form) {
        this.form = form;
    }

    @Override
    public String getMethod() {
        return "RECV";
    }

    @Override
    public EventType getEventType() {
        return EventType.RECV;
    }

    @Override
    public void execute(Object arg) {
        if (arg instanceof ResponseDto response) {
            handleResponse(response);
        } else {
            log.warn("잘못된 응답 형식: {}", arg != null ? arg.getClass().getName() : "null");
        }
    }

    /**
     * 응답 타입에 따라 적절한 핸들러 호출
     */
    private void handleResponse(ResponseDto response) {
        String type = response.header().type();
        log.debug("응답 처리 - type: {}, success: {}", type, response.header().success());

        if (!response.header().success()) {
            // 실패 응답 처리
            handleError(response);
            return;
        }

        // 성공 응답 처리
        switch (type) {
            case TypeManagement.Auth.LOGIN_SUCCESS -> handleLoginSuccess(response);
            case TypeManagement.Auth.LOGOUT_SUCCESS -> handleLogoutSuccess(response);
            case TypeManagement.Chat.MESSAGE_SUCCESS -> handleChatMessageSuccess(response);
            case TypeManagement.Chat.PRIVATE_SUCCESS -> handlePrivateMessageSuccess(response);
            case TypeManagement.Room.LIST_SUCCESS -> handleRoomListSuccess(response);
            case TypeManagement.Room.CREATE_SUCCESS -> handleRoomCreateSuccess(response);
            case TypeManagement.User.LIST_SUCCESS -> handleUserListSuccess(response);
            case TypeManagement.ERROR -> handleErrorResponse(response);
            default -> log.debug("처리되지 않은 응답 타입: {}", type);
        }
    }

    /**
     * 로그인 성공 처리
     */
    private void handleLoginSuccess(ResponseDto response) {
        if (response.data() instanceof AuthDto.LoginResponse loginResponse) {
            log.info("로그인 성공 처리 - userId: {}", loginResponse.userId());
            form.onLoginSuccess(
                loginResponse.userId(),
                loginResponse.sessionId(),
                loginResponse.message()
            );
        }
    }

    /**
     * 로그아웃 성공 처리
     */
    private void handleLogoutSuccess(ResponseDto response) {
        if (response.data() instanceof AuthDto.LogoutResponse logoutResponse) {
            log.info("로그아웃 성공 처리");
            form.onLogoutSuccess(logoutResponse.message());
        }
    }

    /**
     * 채팅 메시지 전송 성공 처리
     */
    private void handleChatMessageSuccess(ResponseDto response) {
        if (response.data() instanceof ChatDto.MessageResponse messageResponse) {
            log.debug("채팅 메시지 전송 성공 - messageId: {}", messageResponse.messageId());
            // 메시지 전송 확인 (필요시 UI에 표시)
        }
    }

    /**
     * 귓속말 수신 처리
     */
    private void handlePrivateMessageSuccess(ResponseDto response) {
        if (response.data() instanceof ChatDto.PrivateResponse privateResponse) {
            String timestamp = response.header().timestamp()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            form.appendChatMessage(
                "[귓속말] " + privateResponse.senderId(),
                privateResponse.message(),
                timestamp
            );
        }
    }

    /**
     * 채팅방 목록 응답 처리
     */
    private void handleRoomListSuccess(ResponseDto response) {
        if (response.data() instanceof RoomDto.ListResponse listResponse) {
            log.info("채팅방 목록 수신 - {} 개의 방", listResponse.rooms() != null ? listResponse.rooms().size() : 0);
            form.updateRoomList(listResponse.rooms());
        }
    }

    /**
     * 채팅방 생성 성공 처리
     */
    private void handleRoomCreateSuccess(ResponseDto response) {
        if (response.data() instanceof RoomDto.CreateResponse createResponse) {
            log.info("채팅방 생성 성공 - roomId: {}, roomName: {}", createResponse.roomId(), createResponse.roomName());
            form.onRoomCreated(createResponse.roomId(), createResponse.roomName());
        }
    }

    /**
     * 사용자 목록 응답 처리
     */
    private void handleUserListSuccess(ResponseDto response) {
        if (response.data() instanceof UserDto.UserListResponse listResponse) {
            log.info("사용자 목록 수신 - {} 명의 유저", listResponse.users() != null ? listResponse.users().size() : 0);
            form.updateUserList(listResponse.users());
        }
    }

    /**
     * 에러 응답 처리
     */
    private void handleErrorResponse(ResponseDto response) {
        if (response.data() instanceof ErrorDto errorDto) {
            log.error("서버 에러 - code: {}, message: {}", errorDto.code(), errorDto.message());
            form.showError(errorDto.code(), errorDto.message());
        }
    }

    /**
     * 실패 응답 처리
     */
    private void handleError(ResponseDto response) {
        String type = response.header().type();
        String errorMessage = "요청 처리에 실패했습니다.";

        if (response.data() instanceof ErrorDto errorDto) {
            errorMessage = errorDto.message();
            form.showError(errorDto.code(), errorMessage);
        } else {
            form.showError("UNKNOWN", errorMessage);
        }

        // 로그인 실패의 경우 특별 처리
        if (type.contains("LOGIN")) {
            form.onLoginFailed(errorMessage);
        }
    }
}
