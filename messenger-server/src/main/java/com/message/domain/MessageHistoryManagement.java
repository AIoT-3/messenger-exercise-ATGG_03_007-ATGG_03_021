package com.message.domain;

import com.message.dto.data.impl.ChatDto;
import com.message.entity.chat.WhisperChatEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHistoryManagement {
    // 두 유저 간의 대화 기록을 저장하는 Map (Key: "userA:userB", Value: 대화 목록)
    private static final Map<String, List<ChatDto.PrivateRequest>> whisperHistories = new ConcurrentHashMap<>();

    /**
     * 귓속말 저장
     */
    public static void saveWhisper(ChatDto.PrivateRequest message) {
        String key = getWhisperKey(message.senderId(), message.receiverId());
        whisperHistories.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>())).add(message);
    }

    /**
     * 귓속말 기록 조회
     */
    public static List<ChatDto.PrivateRequest> getWhisperHistory(String user1, String user2) {
        String key = getWhisperKey(user1, user2);
        return whisperHistories.getOrDefault(key, Collections.emptyList());
    }

    /**
     * 두 유저 아이디를 정렬하여 고유 키 생성 (A가 B에게 보내든, B가 A에게 보내든 같은 방으로 취급)
     */
    private static String getWhisperKey(String user1, String user2) {
        if (user1.compareTo(user2) < 0) {
            return user1 + ":" + user2;
        } else {
            return user2 + ":" + user1;
        }
    }
}