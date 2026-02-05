package com.message.dto.data.impl;

import com.fasterxml.jackson.annotation.JsonValue;
import com.message.TypeManagement;
import com.message.dto.data.MessageDataType;
import com.message.dto.data.ResponseDataDto;

import java.util.List;

public class SynchronizedDto {

    @MessageDataType(TypeManagement.Sync.USER)
    public record UserSync(
            @JsonValue
            List<UserDto.UserInfo> users
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Sync.USER;
        }
    }
}
