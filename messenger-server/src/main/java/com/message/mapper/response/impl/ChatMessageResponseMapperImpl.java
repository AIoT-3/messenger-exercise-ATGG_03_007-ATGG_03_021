package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.ChatDto;
import com.message.mapper.response.AbstractResponseMapper;

// TODO 구현사항 (재민)

// 일반 채팅 성공 응답 처리 용도임
public class ChatMessageResponseMapperImpl extends AbstractResponseMapper {
    @Override
    public String getClassName() {
        return ChatDto.MessageResponse.class.getName();
    }

    @Override
    protected String getErrorMessage() {
        return "[채팅] 서버 문제로 결과가 null입니다.]";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof ChatDto.MessageResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        return new ResponseDto(
                createHeader(TypeManagement.Chat.MESSAGE_SUCCESS),
                (ChatDto.MessageResponse) o
        );
    }
}
