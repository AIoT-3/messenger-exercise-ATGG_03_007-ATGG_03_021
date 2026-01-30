package com.message.dto;

import java.util.List;

public class UserDto {
        public record UserInfo(
            String id,
            String name,
            boolean online
        ) {}

        public record UserListResponse(
            List<UserInfo> users
        ) {}
}