package com.message.service.room;

import com.message.dto.data.impl.RoomDto;

public interface RoomService {
    RoomDto.CreateResponse createRoom(String sessionId, RoomDto.CreateRequest request);
    RoomDto.ListResponse getRoomList(String sessionId);
    RoomDto.EnterResponse enterRoom(String sessionId, RoomDto.EnterRequest request);
    void exitRoom(String sessionId, RoomDto.ExitRequest request);
}