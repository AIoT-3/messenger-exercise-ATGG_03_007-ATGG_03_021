package com.message.ui;

import com.message.TypeManagement;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.dto.data.impl.AuthDto;
import com.message.dto.data.impl.ChatDto;
import com.message.session.ClientSession;
import com.message.ui.event.MessageAction;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
public class SendMessageAction implements MessageAction {

    @Override
    public void execute(Object arg) {
        if (!(arg instanceof String text)) {
            return;
        }

        try {
            RequestDto requestDto = createRequest(text);
            if (requestDto != null) {
                ClientSession.send(requestDto);
            }
        } catch (Exception e) {
            log.error("Failed to send message", e);
        }
    }

    private RequestDto createRequest(String text) {
        if (text.startsWith("/login ")) {
            String[] parts = text.split(" ");

            if (parts.length == 3) {
                String userId = parts[1];
                String password = parts[2];
                AuthDto.LoginRequest data = new AuthDto.LoginRequest(userId, password);
                HeaderDto.RequestHeader header = new HeaderDto.RequestHeader(
                        TypeManagement.Auth.LOGIN, OffsetDateTime.now(), ClientSession.getSessionId());
                return new RequestDto(header, data);
            }

        } else if (text.startsWith("/logout")) {
            HeaderDto.RequestHeader header = new HeaderDto.RequestHeader(
                    TypeManagement.Auth.LOGOUT, OffsetDateTime.now(), ClientSession.getSessionId());
            return new RequestDto(header, null);

        } else {
            // Chat message
            if (!ClientSession.isAuthenticated()) {
                log.warn("Not authenticated, cannot send message");
                return null;
            }

            ChatDto.MessageRequest data = new ChatDto.MessageRequest(ClientSession.getCurrentRoomId(), text);
            HeaderDto.RequestHeader header = new HeaderDto.RequestHeader(
                    TypeManagement.Chat.MESSAGE, OffsetDateTime.now(), ClientSession.getSessionId());
            return new RequestDto(header, data);
        }
        return null;
    }
}
