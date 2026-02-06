package com.message.service.chat;

import com.message.dto.data.impl.ChatDto;
import com.message.entity.chat.WhisperChatEntity;

import java.util.List;

public interface ChatService {
    ChatDto.MessageResponse sendChatMessage(String sessionId, ChatDto.MessageRequest request);
    ChatDto.PrivateResponse sendPrivateMessage(String sessionId, ChatDto.PrivateRequest request);
    ChatDto.HistoryResponse getHistory(String sessionId, ChatDto.HistoryRequest request);
    ChatDto.HistoryResponse getHistoryAllByRoomId(long roomId);
    List<String> getRoomInUserIds(long roomId);

    List<ChatDto.PrivateRequest> getPrivateHistory(String sessionId, String targetId);
}