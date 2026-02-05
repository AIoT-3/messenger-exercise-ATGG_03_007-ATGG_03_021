package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.ChatDto;
import com.message.handler.Handler;
import com.message.service.chat.ChatService;
import com.message.service.chat.impl.ChatServiceImpl;

public class ChatMessageHandler implements Handler {
    private final ChatService chatService = new ChatServiceImpl(); // 서비스 주입

    @Override
    public String getMethod() {
        return TypeManagement.Chat.MESSAGE;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        ChatDto.MessageRequest request = (ChatDto.MessageRequest) data; // 요청 데이터 MessageRequest로 캐스팅

        return chatService.sendChatMessage(header.sessionId(), request); // 서비스 호출(세션아이디랑 데이터 전달)
        // 서비스에서 ChatDto.MessageResponse 반환하면 팩토리가 이거를 매퍼한테 전달하게 만들예정
    }
}
