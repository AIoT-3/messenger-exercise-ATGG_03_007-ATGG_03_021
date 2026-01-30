package com.message.domain;

import com.message.entity.RoomEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManagement {
    private static final Map<Long, RoomEntity> rooms = new ConcurrentHashMap<>();

    public void addRoom(RoomEntity room) {
        //TODO 검증 하셈

//        rooms.put(room.getId, room);
    }
}
