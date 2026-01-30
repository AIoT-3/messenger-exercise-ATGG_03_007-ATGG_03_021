package com.message.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoomEntity {
    private final String roomId;
    private String roomName;
    private int userCount;
}