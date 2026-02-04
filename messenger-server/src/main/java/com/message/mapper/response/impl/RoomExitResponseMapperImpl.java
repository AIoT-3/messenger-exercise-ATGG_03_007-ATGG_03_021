package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.RoomDto;
import com.message.mapper.response.AbstractResponseMapper;

public class RoomExitResponseMapperImpl extends AbstractResponseMapper {
    @Override
    public String getClassName() {
        return RoomDto.ExitResponse.class.getName(); // RoomDto 내부에 정의된 ExitResponse
    }

    @Override
    protected String getErrorMessage() {
        return "[채팅방 퇴장] 서버 문제로 결과가 null입니다.";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof RoomDto.ExitResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        return new ResponseDto(
                createHeader(TypeManagement.Room.EXIT_SUCCESS),
                (RoomDto.ExitResponse) o
        );
    }
}