package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.domain.SessionManagement;
import com.message.domain.SocketManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.RoomDto;
import com.message.handler.Handler;
import com.message.mapper.sync.impl.RoomSyncResponseMapper;
import com.message.service.room.RoomService;
import com.message.service.room.impl.RoomServiceImpl;

public class RoomCreateHandler implements Handler {
    private final RoomService roomService = new RoomServiceImpl();
    private final RoomSyncResponseMapper roomSyncResponseMapper = new RoomSyncResponseMapper();

    @Override
    public String getMethod() {
        return TypeManagement.Room.CREATE;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        RoomDto.CreateResponse room = roomService.createRoom((RoomDto.CreateRequest) data);

        RoomDto.ListResponse roomList = roomService.getRoomList();
        sendSynchronizedRooms(roomList);

        return room;
    }

    private void sendSynchronizedRooms(RoomDto.ListResponse roomList){
        String syncResponse = roomSyncResponseMapper.toSyncResponse(roomList.rooms());
        SocketManagement.sendSynchronizedMessage(SessionManagement.getAllSessionIds(), syncResponse);
    }
}
