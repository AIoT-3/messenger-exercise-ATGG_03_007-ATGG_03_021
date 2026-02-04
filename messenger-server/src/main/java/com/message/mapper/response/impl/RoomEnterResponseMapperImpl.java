package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.RoomDto;
import com.message.mapper.response.AbstractResponseMapper;

public class RoomEnterResponseMapperImpl extends AbstractResponseMapper {
    @Override
    public String getClassName() {
        return RoomDto.EnterResponse.class.getName();
    }

    @Override
    protected String getErrorMessage() {
        return "[채팅방 입장] 서버 문제로 결과가 null입니다.";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof RoomDto.EnterResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        // 부모에 createHeader(String type)를 만드셨다면 활용하세요!
        return new ResponseDto(
                createHeader(TypeManagement.Room.ENTER_SUCCESS),
                (RoomDto.EnterResponse) o
        );
    }
}