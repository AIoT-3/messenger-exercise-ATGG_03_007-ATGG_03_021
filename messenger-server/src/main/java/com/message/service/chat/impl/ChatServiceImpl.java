package com.message.service.chat.impl;

import com.message.TypeManagement;
import com.message.domain.*;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.ResponseDataDto;
import com.message.dto.data.impl.ChatDto;
import com.message.entity.RoomEntity;
import com.message.entity.chat.RoomChatEntity;
import com.message.exception.custom.BusinessException;
import com.message.mapper.chat.ChatMapper;
import com.message.mapper.chat.impl.ChatMapperImpl;
import com.message.service.chat.ChatService;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatMapper chatMapper = new ChatMapperImpl();

    @Override
    public ChatDto.MessageResponse sendChatMessage(String sessionId, ChatDto.MessageRequest request) {
        // 방 존재하는지 확인 -> 없으면 ROOM.NOT_FOUND 처리해야됨
        RoomEntity room = RoomManagement.getRoom(request.roomId());
        if (Objects.isNull(room)) {
            log.error("[채팅 실패] 방을 찾을 수 없습니다. roomId: {}", request.roomId());
            throw new BusinessException(ErrorManagement.Room.NOT_FOUND, "채팅방을 찾을 수 없습니다.", 404);
        }

        // 보낸 사람 확인 (유저아이디)
        String senderId = SessionManagement.getUserId(sessionId);

        long messageId = AtomicLongIdManagement.getChatMessageIdSequenceIncreateAndGet();

        // 채팅 엔티티 생성
        RoomChatEntity roomChatEntity = new RoomChatEntity(request.message(), senderId, messageId);

        // 저장
        room.getChatList().add(roomChatEntity);

        // 브로드캐스트
        broadcast(room, roomChatEntity, messageId);

        return new ChatDto.MessageResponse(room.getRoomId(), messageId);
    }

    // 브로드캐스트
    public void broadcast(RoomEntity room, RoomChatEntity entity, long messageId) {
        log.info("[브로드캐스트] 방: {}, 발신자: {}, 메시지ID: {}", room.getRoomName(), entity.getUserId(), messageId);

        // push용 데이터 생성 (매퍼가 알아서 디티오로 변환해줌)
        ChatDto.ChatMessage pushData = chatMapper.toChatMessage(entity, messageId);

        // 공통 패킷 생성, 직렬화
        ResponseDto pushPacket = new ResponseDto(
                new HeaderDto.ResponseHeader(TypeManagement.Chat.MESSAGE_RECEIVE, true, OffsetDateTime.now(), messageId),
                pushData
        );

        // String 변환 없이 객체 통째로 전달 -> SocketManagement에서 알아서 할거임
        for (String targetSessionId : room.getParticipantSessionIds()) {
            SocketManagement.sendMessage(targetSessionId, pushPacket);
        }
    }

    // 귓속말
    @Override
    public ChatDto.PrivateResponse sendPrivateMessage(String sessionId, ChatDto.PrivateRequest request) {
        // 보내는 사람 (나)
        String senderId = SessionManagement.getUserId(sessionId);

        // 받는 사람 세션 찾기 (이거 할라고 SessionManagement에서 메서드 추가함)
        String receiverSessionId = SessionManagement.getSessionId(request.receiverId());

        if (Objects.isNull(receiverSessionId)) {
            log.error("[귓속말] 실패 - 받는 사람을 찾을 수 없습니다 - receiverSessionId: {}", receiverSessionId);
            throw new BusinessException(ErrorManagement.User.NOT_FOUND, "받는 사람을 찾을 수 없습니다", 404);
        }

        // 메시지 아이디 발급
        long messageId = AtomicLongIdManagement.getChatMessageIdSequenceIncreateAndGet();

        // 매퍼 써서 디티오 생성
        ChatDto.ChatMessage privateData = chatMapper.toChatMessage(new RoomChatEntity(request.message(), senderId, messageId), messageId);

        sendToClient(receiverSessionId, TypeManagement.Chat.PRIVATE_MESSAGE_RECEIVE, privateData, messageId);

        return chatMapper.toPrivateResponse(senderId, request.receiverId(), messageId);
    }

    @Override
    public ChatDto.HistoryResponse getHistory(String sessionId, ChatDto.HistoryRequest request) {
        log.debug("[히스토리 조회] sessionId: {}, roomId: {}", sessionId, request.roomId());

        // 방 존재하는지 확인
        RoomEntity room = RoomManagement.getRoom(request.roomId());
        if (Objects.isNull(room)) {
            log.error("[히스토리 실패] 방을 찾을 수 없습니다. roomId: {}", request.roomId());
            throw new BusinessException(ErrorManagement.Room.NOT_FOUND, "해당 채팅방을 찾을 수 없습니다.", 404);
        }

        // 매퍼로 엔티티 리스트를 히스토리 응답 객체로 변환
        return chatMapper.toHistoryResponse(room.getRoomId(), room.getChatList());
    }

    // 중복 부분 줄이려고 만든 공통 메서드
    private void sendToClient(String targetSessionId, String type, ResponseDataDto data, long messageId) {
        // 패킷 포장 (헤더 생성)
        ResponseDto pushPacket = chatMapper.toResponseDto(type, messageId, data);

        // SocketManagement가 내부적으로 제이슨 변환을 처리하므로 그냥 호출만 하면 됨
        SocketManagement.sendMessage(targetSessionId, pushPacket);
    }
}

