package com.message.action.impl;

import com.message.action.MessageAction;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.ErrorDto;
import com.message.dto.data.impl.RoomDto;
import com.message.dto.data.impl.UserDto;
import com.message.subject.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleResponseAction implements MessageAction {
    private static final Logger log = LoggerFactory.getLogger(ConsoleResponseAction.class);

    @Override
    public String getMethod() {
        return "RECV"; // This action responds to RECV events
    }

    @Override
    public EventType getEventType() {
        return EventType.RECV;
    }

    @Override
    public void execute(Object arg) {
        if (!(arg instanceof ResponseDto response)) {
            log.warn("Expected ResponseDto but received: {}", arg != null ? arg.getClass().getName() : "null");
            return;
        }

        log.debug("ConsoleResponseAction received: {}", response);
        System.out.println(); // Add a newline for better formatting
        printResponse(response);
    }

    private void printResponse(ResponseDto response) {
        // Print based on success or failure
        if (response.header().success()) {
            System.out.println("✅ 성공: " + response.header().type());
        } else {
            System.out.println("❌ 실패: " + response.header().type());
        }

        // Print data payload based on its type
        if (response.data() == null) {
            System.out.println("  [No data payload]");
            return;
        }

        Object data = response.data();
        if (data instanceof ErrorDto error) {
            System.out.println("  Error Code: " + error.code());
            System.out.println("  Message: " + error.message());
        } else if (data instanceof RoomDto.ListResponse roomList) {
            System.out.println("  Available Rooms:");
            if (roomList.rooms() == null || roomList.rooms().isEmpty()) {
                System.out.println("    (No rooms available)");
            } else {
                roomList.rooms().forEach(room ->
                        System.out.printf("    - Room %d: %s (%d users)%n",
                                room.roomId(), room.roomName(), room.userCount()) // CORRECTED LINE
                );
            }
        } else if (data instanceof UserDto.UserListResponse userList) {
            System.out.println("  Users in Room:");
            if (userList.users() == null || userList.users().isEmpty()) {
                System.out.println("    (No other users in this room)");
            } else {
                userList.users().forEach(user -> System.out.println("    - " + user.id())); // CORRECTED LINE
            }
        } else if (response.data() != null) {
            // Generic fallback for other DTOs
            System.out.println("  Data: " + response.data().toString());
        }
    }
}