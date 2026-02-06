package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.domain.SessionManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.ChatDto;
import com.message.handler.Handler;
import com.message.mapper.chat.ChatMapper;
import com.message.mapper.chat.impl.ChatMapperImpl;
import com.message.service.chat.ChatService;
import com.message.service.chat.impl.ChatServiceImpl;

import java.util.List;

public class ChatPrivateHistoryHandler implements Handler {
    private final ChatService chatService = new ChatServiceImpl();
    private final ChatMapper chatMapper = new ChatMapperImpl();

    @Override
    public String getMethod() {
        return TypeManagement.Chat.PRIVATE_HISTORY;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        ChatDto.PrivateHistoryRequest request = (ChatDto.PrivateHistoryRequest) data;

        String userId = SessionManagement.getUserId(header.sessionId());

        List<ChatDto.PrivateRequest> privateHistory = chatService.getPrivateHistory(userId, request.targetId());

        return chatMapper.toPrivateHistoryResponse(request.targetId(), privateHistory);
    }
}
