package com.message.mapper.sync.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.SynchronizedDto;
import com.message.dto.data.impl.UserDto;
import com.message.mapper.sync.AbstractSyncResponseMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
public class UserSyncResponseMapper extends AbstractSyncResponseMapper<UserDto.UserInfo> {
    @Override
    public String toSyncResponse(List<UserDto.UserInfo> list) {
        HeaderDto.ResponseHeader responseHeader = new HeaderDto.ResponseHeader(
                TypeManagement.Sync.USER,
                true,
                OffsetDateTime.now(),
                AtomicLongIdManagement.getResponseMessageIdSequenceIncreateAndGet()
        );
        SynchronizedDto.UserSync userSync = new SynchronizedDto.UserSync(list);
        ResponseDto responseDto = new ResponseDto(responseHeader, userSync);
        try {
            return mapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            log.error("[유저 목록 동기화] 매핑 실패");
            throw new RuntimeException(e);
        }
    }
}
