package com.message.mapper.sync.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.ChatDto;
import com.message.dto.data.impl.SynchronizedDto;
import com.message.mapper.sync.AbstractSyncResponseMapper;

import java.time.OffsetDateTime;
import java.util.List;

public class WhisperSyncResponseMapper extends AbstractSyncResponseMapper<ChatDto.PrivateResponse> {

    @Override
    public String toSyncResponse(List<ChatDto.PrivateResponse> list) {
        if(list.size() != 1){
            throw new IllegalArgumentException();
        }

        HeaderDto.ResponseHeader responseHeader = new HeaderDto.ResponseHeader(
                TypeManagement.Sync.PRIVATE_CHAT,
                true,
                OffsetDateTime.now(),
                AtomicLongIdManagement.getResponseMessageIdSequenceIncreateAndGet()
        );
        SynchronizedDto.PrivateSyncResponse privateSyncResponse = mapper.convertValue(list.getFirst(), SynchronizedDto.PrivateSyncResponse.class);
        ResponseDto responseDto = new ResponseDto(responseHeader, privateSyncResponse);

        try {
            return mapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
