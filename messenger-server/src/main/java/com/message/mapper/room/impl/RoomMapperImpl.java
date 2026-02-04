package com.message.mapper.room.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.message.dto.data.impl.RoomDto;
import com.message.mapper.room.RoomMapper;

public class RoomMapperImpl implements RoomMapper {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public RoomDto.CreateRequest toCreateRequest(String value) {
        return mapper.convertValue(value, RoomDto.CreateRequest.class);
    }

    @Override
    public RoomDto.EnterRequest toEnterRequest(JsonNode dataNode) {
        return mapper.convertValue(dataNode, RoomDto.EnterRequest.class);
    }

    @Override
    public RoomDto.ExitRequest toExitRequest(JsonNode dataNode) {
        return mapper.convertValue(dataNode, RoomDto.ExitRequest.class);
    }
}
