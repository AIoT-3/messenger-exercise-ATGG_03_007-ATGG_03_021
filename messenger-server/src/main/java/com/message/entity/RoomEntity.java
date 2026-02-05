package com.message.entity;

import com.message.entity.chat.Chat;
import com.message.entity.chat.WhisperChatEntity;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RoomEntity {
    private final long roomId;
    private String roomName;
    private int userCount;
    private List<Chat> chatList; // 귓속말 할 때 stream으로 잡아내자

    public RoomEntity(long roomId, String roomName, int userCount) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.userCount = userCount;
        this.chatList = new LinkedList<>();
    }

    // 중복 방지 위해 Set 사용
    private final Set<String> participantUserIds = ConcurrentHashMap.newKeySet();

    public void addParticipant(String userId) {
        participantUserIds.add(userId);
    }

    public void removeParticipant(String userId) {
        participantUserIds.remove(userId);
    }

    public int getUserCount() {
        return participantUserIds.size();
    }

    public synchronized void increaseUserCount() {
        userCount++;
    }

    public synchronized void decreaseUserCount() {
        userCount--;
    }

    public synchronized List<Chat> getChatting(String userId){
        return chatList.stream()
                .filter(c -> {
                    if(c instanceof WhisperChatEntity whisperChat){
                        return whisperChat.getReceiverId().equals(userId) || whisperChat.getSenderId().equals(userId);
                    } else {
                        return true;
                    }
                })
                .toList();
    }
}