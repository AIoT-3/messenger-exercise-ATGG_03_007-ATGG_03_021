package com.message.mapper.room;

import com.fasterxml.jackson.databind.JsonNode;
import com.message.dto.data.impl.RoomDto;

public interface RoomMapper {
    RoomDto.CreateRequest toCreateRequest(String value);
    RoomDto.EnterRequest toEnterRequest(JsonNode dataNode);
    RoomDto.ExitRequest toExitRequest(JsonNode dataNode);
}
