package com.message.entity.chat;

import lombok.Getter;

@Getter
public class WhisperChatEntity extends Chat {
    private String senderId;
    private String receiverId;

    public WhisperChatEntity(String message, String senderId, String receiverId) {
        super(message);
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}
