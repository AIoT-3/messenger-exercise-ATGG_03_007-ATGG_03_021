package com.message.entity.chat;

import lombok.Getter;

@Getter
public class RoomChatEntity extends Chat {
    private String userId;
    private long messageId;

    public RoomChatEntity(String message, String userId, long messageId) {
        super(message);
        this.userId = userId;
        this.messageId = messageId;
    }
}
