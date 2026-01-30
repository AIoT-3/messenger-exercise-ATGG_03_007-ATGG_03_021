package com.message.dto;

import java.util.List;

public class RoomDto {
        public record CreateRequest(
            String roomName
        ) {}

        public record CreateResponse(
            long roomId,
            String roomName
        ) {}

        public record ListResponse(
            List<RoomSummary> rooms
        ) {}

        public record RoomSummary(
            long roomId,
            String roomName,
            int userCount
        ) {}

        public record EnterRequest(
            long roomId
        ) {}

        public record EnterResponse(
            long roomId,
            List<String> users
        ) {}

        public record ExitRequest(
            long roomId
        ) {}

        public record ExitResponse(
            long roomId,
            String message
        ) {}
}