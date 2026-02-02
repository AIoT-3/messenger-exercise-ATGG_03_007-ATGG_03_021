package com.message.dto.data.impl;

import com.message.TypeManagement;
import com.message.dto.data.MessageDataType;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.ResponseDataDto;

import java.util.List;

public class ChatDto {

    @MessageDataType(TypeManagement.Chat.MESSAGE)
    public record MessageRequest(
            long roomId,
            String message
    ) implements RequestDataDto {
        @Override
        public String getType() {
            return TypeManagement.Chat.MESSAGE;
        }
    }

    @MessageDataType(TypeManagement.Chat.MESSAGE_SUCCESS)
    public record MessageResponse(
            long roomId,
            long messageId
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Chat.MESSAGE_SUCCESS;
        }
    }

    @MessageDataType(TypeManagement.Chat.PRIVATE)
    public record PrivateRequest(
            String senderId,
            String receiverId,
            String message
    ) implements RequestDataDto {
        @Override
        public String getType() {
            return TypeManagement.Chat.PRIVATE;
        }
    }

    @MessageDataType(TypeManagement.Chat.PRIVATE_SUCCESS)
    public record PrivateResponse(
            String senderId,
            String receiverId,
            String message,
            long messageId
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Chat.PRIVATE_SUCCESS;
        }
    }

    @MessageDataType(TypeManagement.Chat.HISTORY)
    public record HistoryRequest(
            long roomId,
            int limit,
            long beforeMessageId
    ) implements RequestDataDto {
        @Override
        public String getType() {
            return TypeManagement.Chat.HISTORY;
        }
    }

    @MessageDataType(TypeManagement.Chat.HISTORY_SUCCESS)
    public record HistoryResponse(
            long roomId,
            List<ChatMessage> messages,
            boolean hasMore
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Chat.HISTORY_SUCCESS;
        }
    }

    public record ChatMessage(
            long messageId,
            String senderId,
            String senderName,
            String timestamp,
            String content
    ) {}
}