package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.ChatDto;
import com.message.mapper.response.AbstractResponseMapper;

public class ChatHistoryResponseMapperImpl extends AbstractResponseMapper {
    @Override
    public String getClassName() {
        return ChatDto.HistoryResponse.class.getName();
    }

    @Override
    protected String getErrorMessage() {
        return "[히스토리] 서버 문제로 결과가 null입니다.";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof ChatDto.HistoryResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        return new ResponseDto(
                createHeader(TypeManagement.Chat.HISTORY_SUCCESS),
                (ChatDto.HistoryResponse) o
        );
    }
}