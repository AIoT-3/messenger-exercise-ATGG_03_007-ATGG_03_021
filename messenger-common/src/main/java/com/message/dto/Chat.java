package com.message.dto;

import java.util.List;

public class Chat {
        public record MessageRequest(
            long roomId,
            String message
        ) {}

        public record MessageResponse(
            long roomId,
            long messageId
        ) {}

        public record PrivateRequest(
            String senderId,
            String receiverId,
            String message
        ) {}

        public record PrivateResponse(
            String senderId,
            String receiverId,
            String message,
            long messageId
        ) {}

        public record HistoryRequest(
            long roomId,
            int limit,
            long beforeMessageId
        ) {}

        public record HistoryResponse(
            long roomId,
            List<ChatMessage> messages,
            boolean hasMore
        ) {}

        public record ChatMessage(
            long messageId,
            String senderId,
            String senderName,
            String timestamp,
            String content
        ) {}
}