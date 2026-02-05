package com.message.mapper.chat.impl;

import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.ResponseDataDto;
import com.message.dto.data.impl.ChatDto;
import com.message.entity.chat.Chat;
import com.message.entity.chat.RoomChatEntity;
import com.message.mapper.chat.ChatMapper;

import java.time.OffsetDateTime;
import java.util.List;

public class ChatMapperImpl implements ChatMapper {

    @Override
    public ChatDto.ChatMessage toChatMessage(RoomChatEntity entity, long messageId) {
        // 엔티티 -> 전송용 디티오 변환
        return new ChatDto.ChatMessage(
                messageId,
                entity.getUserId(),
                entity.getUserId(), // 이거 닉네임 같은 거 생기면 바꿔야 함
                OffsetDateTime.now().toString(),
                entity.getMessage()
        );
    }

    @Override
    public ChatDto.HistoryResponse toHistoryResponse(long roomId, List<Chat> chatEntityList) {
        // 엔티티 리스트를 ChatMessage(디티오) 리스트로 변환
        List<ChatDto.ChatMessage> messages = chatEntityList.stream()
                .map(chat -> {
                    RoomChatEntity entity = (RoomChatEntity) chat;

                    return toChatMessage(entity, entity.getMessageId());
                })
                .toList();

        // TODO
        // 페이징 처리 위해 데이터 더 있는지 알려주는 필드 false -> 지금은 페이징 로직 없이 방에 있는 모든 채팅 리스트 한 번에 다 가져오고 있으니까
        // 지금은 일단 false 유지하되, 차후 로직 분리
        return new ChatDto.HistoryResponse(roomId, messages, false);
    }

    @Override
    public ChatDto.PrivateResponse toPrivateResponse(String senderId, String receiverId, long messageId) {
        return new ChatDto.PrivateResponse(
                senderId,
                receiverId,
                "귓속말이 전송되었습니다.",
                messageId
        );
    }

    @Override
    public ResponseDto toResponseDto(String type, long messageId, ResponseDataDto data){
        return new ResponseDto(
                new HeaderDto.ResponseHeader(type, true, OffsetDateTime.now(), messageId),
                data
        );
    }
}
