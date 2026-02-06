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

public class RoomExitHandler implements Handler {
    private final RoomService roomService = new RoomServiceImpl();
    private final RoomSyncResponseMapper roomSyncResponseMapper = new RoomSyncResponseMapper();

    @Override
    public String getMethod() { return TypeManagement.Room.EXIT; }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        RoomDto.ExitRequest request = (RoomDto.ExitRequest) data;
        roomService.exitRoom(header.sessionId(), request);
        RoomDto.ExitResponse exitResponse = new RoomDto.ExitResponse(request.roomId(), "채팅방에서 나갔습니다.");

        RoomDto.ListResponse roomList = roomService.getRoomList();
        sendSynchronizedRooms(roomList);

        return exitResponse;
    }

    private void sendSynchronizedRooms(RoomDto.ListResponse roomList){
        String syncResponse = roomSyncResponseMapper.toSyncResponse(roomList.rooms());
        SocketManagement.sendSynchronizedMessage(SessionManagement.getAllSessionIds(), syncResponse);
    }
}
