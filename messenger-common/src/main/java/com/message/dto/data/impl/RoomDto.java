package com.message.dto.data.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.message.TypeManagement;
import com.message.dto.data.MessageDataType;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.ResponseDataDto;

import java.util.List;



public class RoomDto {

    @MessageDataType(TypeManagement.Room.CREATE)
    public record CreateRequest(
            @JsonProperty("roomName") String roomName
    ) implements RequestDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.CREATE;
        }
    }

    @MessageDataType(TypeManagement.Room.CREATE_SUCCESS)
    public record CreateResponse(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("roomName") String roomName
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.CREATE_SUCCESS;
        }
    }

    @MessageDataType(TypeManagement.Room.LIST_SUCCESS)
    public record ListResponse(
            @JsonProperty("rooms") List<RoomSummary> rooms
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.LIST_SUCCESS;
        }
    }

    public record RoomSummary(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("roomName") String roomName,
            @JsonProperty("userCount") int userCount
    ) {}

    @MessageDataType(TypeManagement.Room.ENTER)
    public record EnterRequest(
            @JsonProperty("roomId") long roomId
    ) implements RequestDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.ENTER;
        }
    }

    @MessageDataType(TypeManagement.Room.ENTER_SUCCESS)
    public record EnterResponse(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("users") List<String> users
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.ENTER_SUCCESS;
        }
    }

    @MessageDataType(TypeManagement.Room.EXIT)
    public record ExitRequest(
            @JsonProperty("roomId") long roomId
    ) implements RequestDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.EXIT;
        }
    }

    @MessageDataType(TypeManagement.Room.EXIT_SUCCESS)
    public record ExitResponse(
            @JsonProperty("roomId") long roomId,
            @JsonProperty("message") String message
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.EXIT_SUCCESS;
        }
    }
}