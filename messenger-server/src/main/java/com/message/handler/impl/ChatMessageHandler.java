package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.domain.SessionManagement;
import com.message.domain.SocketManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.ChatDto;
import com.message.handler.Handler;
import com.message.mapper.sync.impl.ChatHistorySyncResponseMapper;
import com.message.service.chat.ChatService;
import com.message.service.chat.impl.ChatServiceImpl;

import java.util.List;

public class ChatMessageHandler implements Handler {
    private final ChatService chatService = new ChatServiceImpl(); // 서비스 주입
    private final ChatHistorySyncResponseMapper chatHistorySyncResponseMapper = new ChatHistorySyncResponseMapper();

    @Override
    public String getMethod() {
        return TypeManagement.Chat.MESSAGE;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        ChatDto.MessageRequest request = (ChatDto.MessageRequest) data; // 요청 데이터 MessageRequest로 캐스팅

        ChatDto.MessageResponse messageResponse = chatService.sendChatMessage(header.sessionId(), request);// 서비스 호출(세션아이디랑 데이터 전달)

        ChatDto.HistoryResponse historyAll = chatService.getHistoryAllByRoomId(((ChatDto.MessageRequest) data).roomId());

        sendSynchronizedHistoryAll(historyAll.roomId(), historyAll.messages());

        return messageResponse;
    }

    private void sendSynchronizedHistoryAll(long roomId, List<ChatDto.ChatMessage> historyList) {
        List<String> roomInUserIds = chatService.getRoomInUserIds(roomId);
        List<String> sessionIdList = SessionManagement.getSessionIdList(roomInUserIds);

        chatHistorySyncResponseMapper.setRoomId(roomId);
        String syncResponse = chatHistorySyncResponseMapper.toSyncResponse(historyList);

        SocketManagement.sendSynchronizedMessage(sessionIdList, syncResponse);
    }
}
