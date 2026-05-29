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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMapper chatMapper;
    private final SessionManagement sessionManagement;
    private final RoomManagement roomManagement;
    private final AtomicLongIdManagement idManagement;
    private final MessageHistoryManagement historyManagement;
    private final SocketManagement socketManagement;

    public ChatServiceImpl() {
        this(
            new ChatMapperImpl(),
            SessionManagement.getInstance(),
            RoomManagement.getInstance(),
            AtomicLongIdManagement.getInstance(),
            MessageHistoryManagement.getInstance(),
            SocketManagement.getInstance()
        );
    }

    ChatServiceImpl(ChatMapper chatMapper,
                    SessionManagement sessionManagement,
                    RoomManagement roomManagement,
                    AtomicLongIdManagement idManagement,
                    MessageHistoryManagement historyManagement,
                    SocketManagement socketManagement) {
        this.chatMapper = chatMapper;
        this.sessionManagement = sessionManagement;
        this.roomManagement = roomManagement;
        this.idManagement = idManagement;
        this.historyManagement = historyManagement;
        this.socketManagement = socketManagement;
    }

    @Override
    public ChatDto.MessageResponse sendChatMessage(String sessionId, ChatDto.MessageRequest request) {
        RoomEntity room = roomManagement.getRoom(request.roomId());
        if (Objects.isNull(room)) {
            log.error("[채팅 실패] 방을 찾을 수 없습니다. roomId: {}", request.roomId());
            throw new BusinessException(ErrorManagement.Room.NOT_FOUND, "채팅방을 찾을 수 없습니다.", 404);
        }

        String senderId = sessionManagement.getUserId(sessionId);
        long messageId = idManagement.getChatMessageIdSequenceIncrementAndGet();
        RoomChatEntity roomChatEntity = new RoomChatEntity(request.message(), senderId, messageId);
        room.getChatList().add(roomChatEntity);

        return new ChatDto.MessageResponse(room.getRoomId(), messageId);
    }

    public void broadcast(RoomEntity room, RoomChatEntity entity, long messageId) {
        log.info("[브로드캐스트] 방: {}, 발신자: {}, 메시지ID: {}", room.getRoomName(), entity.getUserId(), messageId);

        ChatDto.ChatMessage pushData = chatMapper.toChatMessage(entity, messageId);
        ResponseDto pushPacket = new ResponseDto(
                new HeaderDto.ResponseHeader(TypeManagement.Chat.MESSAGE_RECEIVE, true, OffsetDateTime.now(), messageId),
                pushData
        );

        for (String targetSessionId : room.getParticipantUserIds()) {
            socketManagement.sendMessage(targetSessionId, pushPacket);
        }
    }

    @Override
    public ChatDto.PrivateResponse sendPrivateMessage(String sessionId, ChatDto.PrivateRequest request) {
        String senderId = sessionManagement.getUserId(sessionId);
        String receiverSessionId = sessionManagement.getSessionId(request.receiverId());

        if (Objects.isNull(receiverSessionId)) {
            log.error("[귓속말] 실패 - 받는 사람을 찾을 수 없습니다 - receiverId: {}", request.receiverId());
            throw new BusinessException(ErrorManagement.User.NOT_FOUND, "받는 사람을 찾을 수 없습니다", 404);
        }

        long messageId = idManagement.getChatMessageIdSequenceIncrementAndGet();
        ChatDto.PrivateResponse privateResponse = chatMapper.toPrivateResponse(senderId, request.receiverId(), messageId);
        historyManagement.saveWhisper(request);

        return privateResponse;
    }

    @Override
    public ChatDto.HistoryResponse getHistory(String sessionId, ChatDto.HistoryRequest request) {
        log.debug("[히스토리 조회] sessionId: {}, roomId: {}", sessionId, request.roomId());

        RoomEntity room = roomManagement.getRoom(request.roomId());
        if (Objects.isNull(room)) {
            log.error("[히스토리 실패] 방을 찾을 수 없습니다. roomId: {}", request.roomId());
            throw new BusinessException(ErrorManagement.Room.NOT_FOUND, "해당 채팅방을 찾을 수 없습니다.", 404);
        }

        return chatMapper.toHistoryResponse(room.getRoomId(), room.getChatList());
    }

    @Override
    public ChatDto.HistoryResponse getHistoryAllByRoomId(long roomId) {
        RoomEntity room = roomManagement.getRoom(roomId);
        return chatMapper.toHistoryResponse(room.getRoomId(), room.getChatList());
    }

    @Override
    public List<String> getRoomInUserIds(long roomId) {
        RoomEntity room = roomManagement.getRoom(roomId);
        return new ArrayList<>(room.getParticipantUserIds());
    }

    private void sendToClient(String targetSessionId, String type, ResponseDataDto data, long messageId) {
        ResponseDto pushPacket = chatMapper.toResponseDto(type, messageId, data);
        socketManagement.sendMessage(targetSessionId, pushPacket);
    }

    @Override
    public List<ChatDto.PrivateRequest> getPrivateHistory(String sessionId, String targetId) {
        if (Objects.isNull(sessionId) || Objects.isNull(targetId) || sessionId.isBlank() || targetId.isBlank()) {
            throw new BusinessException(ErrorManagement.Request.IS_NULL, "세션ID 또는 대상 유저ID가 유효하지 않습니다.", 400);
        }
        return historyManagement.getWhisperHistory(sessionId, targetId);
    }
}
