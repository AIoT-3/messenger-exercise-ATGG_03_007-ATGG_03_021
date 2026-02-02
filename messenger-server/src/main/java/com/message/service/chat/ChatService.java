package com.message.service.chat;

import com.message.dto.data.impl.ChatDto;

public interface ChatService {
    ChatDto.MessageResponse sendChatMessage(String sessionId, ChatDto.MessageRequest request);
    ChatDto.PrivateResponse sendPrivateMessage(String sessionId, ChatDto.PrivateRequest request);
    ChatDto.HistoryResponse getHistory(String sessionId, ChatDto.HistoryRequest request);
}