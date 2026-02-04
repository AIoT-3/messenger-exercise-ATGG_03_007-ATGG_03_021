package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.RoomDto;
import com.message.mapper.response.AbstractResponseMapper;

public class RoomListResponseMapperImpl extends AbstractResponseMapper {
    @Override
    public String getClassName() {
        return RoomDto.ListResponse.class.getName();
    }

    @Override
    protected String getErrorMessage() {
        return "[채팅방 리스트] 서버 문제로 결과가 null입니다.";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof RoomDto.ListResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        return new ResponseDto(
                createHeader(TypeManagement.Room.LIST_SUCCESS),
                (RoomDto.ListResponse) o
        );
    }
}