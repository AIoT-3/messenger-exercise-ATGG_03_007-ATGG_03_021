package com.message.service.room;

import com.message.dto.data.impl.RoomDto;

public interface RoomService {
    RoomDto.CreateResponse createRoom(RoomDto.CreateRequest request);
    RoomDto.ListResponse getRoomList();
    RoomDto.EnterResponse enterRoom(String sessionId, RoomDto.EnterRequest request);
    void exitRoom(String sessionId, RoomDto.ExitRequest request);

}