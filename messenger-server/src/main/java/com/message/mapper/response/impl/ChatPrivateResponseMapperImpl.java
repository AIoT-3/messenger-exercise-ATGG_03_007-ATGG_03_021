package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.ChatDto;
import com.message.mapper.response.AbstractResponseMapper;

public class ChatPrivateResponseMapperImpl extends AbstractResponseMapper {
    @Override
    public String getClassName() {
        return ChatDto.PrivateResponse.class.getName();
    }

    @Override
    protected String getErrorMessage() {
        return "[귓속말] 결과 전송 중 오류 발생";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof ChatDto.PrivateResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        return new ResponseDto(
                createHeader(TypeManagement.Chat.PRIVATE_SUCCESS),
                (ChatDto.PrivateResponse) o
        );
    }
}