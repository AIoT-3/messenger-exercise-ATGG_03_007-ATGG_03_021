package com.message.mapper.sync.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.ChatDto;
import com.message.dto.data.impl.SynchronizedDto;
import com.message.mapper.sync.AbstractSyncResponseMapper;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Setter
public class ChatHistorySyncResponseMapper extends AbstractSyncResponseMapper<ChatDto.ChatMessage> {
    private Long roomId;

    @Override
    public String toSyncResponse(List<ChatDto.ChatMessage> list) {
        if(Objects.isNull(roomId)){
            throw new IllegalArgumentException();
        }

        HeaderDto.ResponseHeader responseHeader = new HeaderDto.ResponseHeader(
                TypeManagement.Sync.ROOM_CHAT,
                true,
                OffsetDateTime.now(),
                AtomicLongIdManagement.getResponseMessageIdSequenceIncreateAndGet()
        );
        SynchronizedDto.HistorySyncResponse historySyncResponse = new SynchronizedDto.HistorySyncResponse(roomId, list, false);
        ResponseDto responseDto = new ResponseDto(responseHeader, historySyncResponse);

        try {
            return mapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            roomId = null;
        }
    }

}
