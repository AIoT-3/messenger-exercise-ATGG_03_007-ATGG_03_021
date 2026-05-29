package com.message.domain;

import com.message.dto.data.impl.ChatDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHistoryManagement {

    private static final MessageHistoryManagement INSTANCE = new MessageHistoryManagement();

    private final Map<String, List<ChatDto.PrivateRequest>> whisperHistories = new ConcurrentHashMap<>();

    private MessageHistoryManagement() {}

    public static MessageHistoryManagement getInstance() {
        return INSTANCE;
    }

    public void saveWhisper(ChatDto.PrivateRequest message) {
        String key = getWhisperKey(message.senderId(), message.receiverId());
        whisperHistories.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>())).add(message);
    }

    public List<ChatDto.PrivateRequest> getWhisperHistory(String user1, String user2) {
        String key = getWhisperKey(user1, user2);
        return whisperHistories.getOrDefault(key, Collections.emptyList());
    }

    private String getWhisperKey(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + ":" + user2 : user2 + ":" + user1;
    }
}
