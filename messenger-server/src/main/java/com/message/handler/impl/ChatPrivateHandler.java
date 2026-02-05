package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.domain.SessionManagement;
import com.message.domain.SocketManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.ChatDto;
import com.message.handler.Handler;
import com.message.mapper.sync.impl.WhisperSyncResponseMapper;
import com.message.service.chat.ChatService;
import com.message.service.chat.impl.ChatServiceImpl;

import java.util.List;

public class ChatPrivateHandler implements Handler {
    private final ChatService chatService = new ChatServiceImpl();
    private final WhisperSyncResponseMapper whisperSyncResponseMapper = new WhisperSyncResponseMapper();

    @Override
    public String getMethod() {
        return TypeManagement.Chat.PRIVATE;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        ChatDto.PrivateRequest request = (ChatDto.PrivateRequest) data;

        ChatDto.PrivateResponse privateResponse = chatService.sendPrivateMessage(header.sessionId(), request);

        sendSynchronizedPrivate(privateResponse);

        return privateResponse;
    }

    private void sendSynchronizedPrivate(ChatDto.PrivateResponse privateResponse) {
        List<String> sessionIdList = List.of(SessionManagement.getSessionId(privateResponse.receiverId()));

        String syncResponse = whisperSyncResponseMapper.toSyncResponse(List.of(privateResponse));

        SocketManagement.sendSynchronizedMessage(sessionIdList, syncResponse);
    }
}
