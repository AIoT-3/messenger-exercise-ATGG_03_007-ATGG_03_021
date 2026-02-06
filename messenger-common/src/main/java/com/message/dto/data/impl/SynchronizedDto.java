package com.message.dto.data.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @MessageDataType(TypeManagement.Sync.ROOM)
    public record RoomListSync(
            @JsonProperty("rooms") List<RoomDto.RoomSummary> rooms
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.LIST_SUCCESS;
        }
    }

    @MessageDataType(TypeManagement.Sync.ROOM_CHAT)
    public record HistorySyncResponse(
            long roomId,
            List<ChatDto.ChatMessage> messages,
            boolean hasMore
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Sync.ROOM_CHAT;
        }
    }

    @MessageDataType(TypeManagement.Sync.PRIVATE_CHAT)
    public record PrivateSyncResponse(
            String senderId,
            String receiverId,
            String message,
            long messageId
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Sync.PRIVATE_CHAT;
        }
    }
}
