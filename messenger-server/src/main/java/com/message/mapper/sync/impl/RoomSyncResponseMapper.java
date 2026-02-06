package com.message.mapper.sync.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.RoomDto;
import com.message.dto.data.impl.SynchronizedDto;
import com.message.mapper.sync.AbstractSyncResponseMapper;

import java.time.OffsetDateTime;
import java.util.List;

public class RoomSyncResponseMapper extends AbstractSyncResponseMapper<RoomDto.RoomSummary> {
    @Override
    public String toSyncResponse(List<RoomDto.RoomSummary> list) {
        HeaderDto.ResponseHeader responseHeader = new HeaderDto.ResponseHeader(
                TypeManagement.Sync.ROOM,
                true,
                OffsetDateTime.now(),
                AtomicLongIdManagement.getResponseMessageIdSequenceIncreateAndGet()
        );
        SynchronizedDto.RoomListSync roomListSync = new SynchronizedDto.RoomListSync(list);
        ResponseDto responseDto = new ResponseDto(responseHeader, roomListSync);
        try {
            return mapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
