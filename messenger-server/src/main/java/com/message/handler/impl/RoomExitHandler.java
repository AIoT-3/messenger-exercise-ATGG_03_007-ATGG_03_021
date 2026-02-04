package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.RoomDto;
import com.message.handler.Handler;
import com.message.service.room.RoomService;
import com.message.service.room.impl.RoomServiceImpl;

public class RoomExitHandler implements Handler {
    private final RoomService roomService = new RoomServiceImpl();

    @Override
    public String getMethod() { return TypeManagement.Room.EXIT; }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        RoomDto.ExitRequest request = (RoomDto.ExitRequest) data;
        roomService.exitRoom(header.sessionId(), request);
        return new RoomDto.ExitResponse(request.roomId(), "채팅방에서 나갔습니다.");
    }
}
