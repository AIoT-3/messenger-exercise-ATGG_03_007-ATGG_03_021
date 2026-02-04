package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.RoomDto;
import com.message.handler.Handler;
import com.message.service.room.RoomService;
import com.message.service.room.impl.RoomServiceImpl;

public class RoomCreateHandler implements Handler {
    private final RoomService roomService = new RoomServiceImpl();

    @Override
    public String getMethod() {
        return TypeManagement.Room.CREATE;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        return roomService.createRoom((RoomDto.CreateRequest) data);
    }
}
