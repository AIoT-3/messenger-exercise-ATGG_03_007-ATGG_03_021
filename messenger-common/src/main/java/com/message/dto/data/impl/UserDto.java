package com.message.dto.data.impl;

import com.message.TypeManagement;
import com.message.dto.data.MessageDataType;
import com.message.dto.data.ResponseDataDto;

import java.util.List;

public class UserDto {
    public record UserInfo(
            String id,
            String name,
            boolean online
    ) {}

    @MessageDataType(TypeManagement.User.LIST_SUCCESS)
    public record UserListResponse(
            List<UserInfo> users
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.User.LIST_SUCCESS;
        }
    }
}