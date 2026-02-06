package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.ChatDto;
import com.message.mapper.response.AbstractResponseMapper;

public class ChatPrivateHistoryResponseMapper extends AbstractResponseMapper {
    @Override
    protected String getErrorMessage() {
        return "[귓속말 히스토리] 서버 문제로 결과가 null입니다.";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof ChatDto.PrivateHistoryResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        return new ResponseDto(
                createHeader(TypeManagement.Chat.PRIVATE_HISTORY_SUCCESS),
                (ChatDto.PrivateHistoryResponse) o);
    }

    @Override
    public String getClassName() {
        return ChatDto.PrivateHistoryResponse.class.getName();
    }
}
