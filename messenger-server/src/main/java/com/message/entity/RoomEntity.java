package com.message.entity;

import com.message.domain.AtomicLongIdManagement;
import lombok.Getter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RoomEntity {
    private final long roomId;
    private String roomName;
    private int userCount;

    public RoomEntity(String roomName, int userCount) {
        this.roomId = AtomicLongIdManagement.getRoomIdSequenceIncreateAndGet();
        this.roomName = roomName;
        this.userCount = userCount;
    }

    // 중복 방지 위해 Set 사용
    private final Set<String> participantSessionIds = ConcurrentHashMap.newKeySet();

    public void addParticipant(String sessionId) {
        participantSessionIds.add(sessionId);
    }

    public void removeParticipant(String sessionId) {
        participantSessionIds.remove(sessionId);
    }

    public int getUserCount() {
        return participantSessionIds.size();
    }

    public synchronized void increaseUserCount() {
        userCount++;
    }

    public synchronized void decreaseUserCount() {
        userCount--;
    }
}