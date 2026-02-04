package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.RoomDto;
import com.message.mapper.response.AbstractResponseMapper;

public class RoomCreateResponseMapperImpl extends AbstractResponseMapper {
    @Override
    public String getClassName() {
        return RoomDto.CreateResponse.class.getName();
    }

    @Override
    protected String getErrorMessage() {
        return "[채팅방 생성] 서버 문제로 결과가 null입니다.";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof RoomDto.CreateResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        return new ResponseDto(
                createHeader(TypeManagement.Room.CREATE_SUCCESS),
                (RoomDto.CreateResponse) o
        );
    }
}