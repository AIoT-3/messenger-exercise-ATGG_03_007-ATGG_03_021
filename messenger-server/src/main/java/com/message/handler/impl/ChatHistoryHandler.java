package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.ChatDto;
import com.message.handler.Handler;
import com.message.service.chat.ChatService;
import com.message.service.chat.impl.ChatServiceImpl;

public class ChatHistoryHandler implements Handler {
    private final ChatService chatService = new ChatServiceImpl();

    @Override
    public String getMethod() {
        return TypeManagement.Chat.HISTORY;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        ChatDto.HistoryRequest request = (ChatDto.HistoryRequest) data;

        return chatService.getHistory(header.sessionId(), request);
    }
}
