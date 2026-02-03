package com.message.domain;

import com.fasterxml.jackson.databind.JsonNode;

public class MessageContent {
    public interface Message {
        String getType();
    }

    public record RequestMessage(
            String type,
            String content
    ) implements Message{
        @Override
        public String getType() {
            return type;
        }
    }

    public record ResponseMessage(
            String type,
            JsonNode header,
            JsonNode data
    ) implements Message{
        @Override
        public String getType() {
            return type;
        }
    }
}
