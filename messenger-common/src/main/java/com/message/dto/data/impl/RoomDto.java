package com.message.dto.data.impl;

import com.message.TypeManagement;
import com.message.dto.data.MessageDataType;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.ResponseDataDto;

import java.util.List;

public class RoomDto {

    @MessageDataType(TypeManagement.Room.CREATE)
    public record CreateRequest(
            String roomName
    ) implements RequestDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.CREATE;
        }
    }

    @MessageDataType(TypeManagement.Room.CREATE_SUCCESS)
    public record CreateResponse(
            long roomId,
            String roomName
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.CREATE_SUCCESS;
        }
    }

    @MessageDataType(TypeManagement.Room.LIST_SUCCESS)
    public record ListResponse(
            List<RoomSummary> rooms
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.LIST_SUCCESS;
        }
    }

    public record RoomSummary(
            long roomId,
            String roomName,
            int userCount
    ) {}

    @MessageDataType(TypeManagement.Room.ENTER)
    public record EnterRequest(
            long roomId
    ) implements RequestDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.ENTER;
        }
    }

    @MessageDataType(TypeManagement.Room.ENTER_SUCCESS)
    public record EnterResponse(
            long roomId,
            List<String> users
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.ENTER_SUCCESS;
        }
    }

    @MessageDataType(TypeManagement.Room.EXIT)
    public record ExitRequest(
            long roomId
    ) implements RequestDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.EXIT;
        }
    }

    @MessageDataType(TypeManagement.Room.EXIT_SUCCESS)
    public record ExitResponse(
            long roomId,
            String message
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Room.EXIT_SUCCESS;
        }
    }
}