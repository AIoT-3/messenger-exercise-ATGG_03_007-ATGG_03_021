package com.message.domain;

import com.message.entity.MessageEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHistoryManagement {
    private static final Map<Long, List<MessageEntity>> messageHistories = new ConcurrentHashMap<>();
}
