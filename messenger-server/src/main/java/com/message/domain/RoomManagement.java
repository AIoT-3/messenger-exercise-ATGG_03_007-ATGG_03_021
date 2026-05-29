package com.message.domain;

import com.message.entity.RoomEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManagement {

    private static final RoomManagement INSTANCE = new RoomManagement();

    private final Map<Long, RoomEntity> rooms = new ConcurrentHashMap<>();

    private RoomManagement() {}

    public static RoomManagement getInstance() {
        return INSTANCE;
    }

    public void addRoom(RoomEntity room) {
        rooms.put(room.getRoomId(), room);
    }

    public RoomEntity getRoom(long roomId) {
        return rooms.get(roomId);
    }

    public List<RoomEntity> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }
}
