package com.message.domain;

import com.message.entity.RoomEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManagement {
    private static final Map<Long, RoomEntity> rooms = new ConcurrentHashMap<>();

    public static void addRoom(RoomEntity room) {
        rooms.put(room.getRoomId(), room);
    }

    public static RoomEntity getRoom(long roomId) {
        return rooms.get(roomId);
    }

    public static List<RoomEntity> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }
}
