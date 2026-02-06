package com.message.mapper.chat;

import com.message.dto.ResponseDto;
import com.message.dto.data.ResponseDataDto;
import com.message.dto.data.impl.ChatDto;
import com.message.entity.chat.Chat;
import com.message.entity.chat.RoomChatEntity;

import com.message.entity.chat.WhisperChatEntity;

import java.util.List;

public interface ChatMapper {
    // 엔티티 -> ChatMessage DTO 변환
    ChatDto.ChatMessage toChatMessage(RoomChatEntity entity, long messageId);

    // 귓속말 엔티티 -> ChatMessage DTO 변환
    ChatDto.ChatMessage toChatMessage(WhisperChatEntity entity, long messageId);

    // 엔티티 리스트 -> HistoryResponse 변환 (getHistory 할 때 씀)
    ChatDto.HistoryResponse toHistoryResponse(long roomId, List<Chat> chatEntityList);

    // 귓속말 리스트 -> PrivateHistoryResponse 변환
    ChatDto.PrivateHistoryResponse toPrivateHistoryResponse(String targetId, List<ChatDto.PrivateRequest> privateResponseList);

    // 귓속말 전송 성공 응답 생성
    ChatDto.PrivateResponse toPrivateResponse(String senderId, String receiverId, long messageId);

    ResponseDto toResponseDto(String type, long messageId, ResponseDataDto data);
}
