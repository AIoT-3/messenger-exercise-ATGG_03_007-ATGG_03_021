package com.message.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.message.domain.MessageContent;
import com.message.subject.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageRoute {
    private static final Logger log = LoggerFactory.getLogger(MessageRoute.class);

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static synchronized MessageContent.Message getMessageContent(EventType eventType, String message) {
        if (eventType.equals(EventType.SEND)) {
            return getRequestMessageContent(message);
        } else if (eventType.equals(EventType.RECV)) {
            return getResponseMessageContent(message);
        }

        throw new IllegalArgumentException();
    }

    private static MessageContent.RequestMessage getRequestMessageContent(String message) {
        String trim = message.trim();
        int i = trim.indexOf(" ");
        if (i > 0) {
            // type: 첫 번째 공백 전까지, content: 첫 번째 공백 이후
            String type = trim.substring(0, i);
            String content = trim.substring(i + 1);
            log.debug("[메시지 루트] 요청 파싱 - type: {}, content: {}", type, content);
            return new MessageContent.RequestMessage(type, content);
        } else {
            // 공백이 없으면 전체가 type (예: LOGOUT)
            log.debug("[메시지 루트] 요청 파싱 - type: {}, content: null", trim);
            return new MessageContent.RequestMessage(trim, null);
        }
    }

    private static MessageContent.ResponseMessage getResponseMessageContent(String message) {
        try {
            JsonNode jsonNode = mapper.readTree(message);
            JsonNode header = jsonNode.path("header");
            JsonNode data = jsonNode.path("data");
            log.debug("[메시지 루트] 메시지 헤더 파싱 성공");
            return new MessageContent.ResponseMessage(header.path("type").asText(), header, data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}