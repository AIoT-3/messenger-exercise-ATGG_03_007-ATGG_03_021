package com.message.action;

import com.message.action.impl.RequestMessageAction;
import com.message.action.impl.ResponseMessageAction;
import com.message.subject.EventType;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MessageActionFactory {
    private static final Logger log = LoggerFactory.getLogger(MessageActionFactory.class);
    private final static Map<String, RequestMessageAction> requestMessageActionMap = new HashMap<>();
    private final static Map<String, ResponseMessageAction> responseMessageActionHashMap = new HashMap<>();

    static {
        // "com.message.command" 패키지 내의 Command 하위 타입들을 검색합니다.
        Reflections reflections = new Reflections("com.message.ui.action");
        Set<Class<? extends MessageAction>> classes = reflections.getSubTypesOf(MessageAction.class);

        for (Class<? extends MessageAction> clazz : classes) {
            try {
                // 각 핸들러 클래스의 인스턴스를 생성합니다.
                MessageAction messageAction = clazz.getDeclaredConstructor().newInstance();
                log.debug("[CommandFactory] CommandFactory 초기화 - instance: {}", messageAction.getClass().getName());
                if(messageAction.getEventType().equals(EventType.SEND)) {
                    requestMessageActionMap.put(messageAction.getMethod(), (RequestMessageAction) messageAction);
                } else if(messageAction.getEventType().equals(EventType.RECV)) {
                    responseMessageActionHashMap.put(messageAction.getMethod(), (ResponseMessageAction) messageAction);
                } else {
                    throw new IllegalArgumentException();
                }
                // 핸들러의 메서드 이름을 키로 하여 맵에 등록합니다.
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public static ResponseMessageAction getResponseMessageAction(String key) {
        ResponseMessageAction messageAction = responseMessageActionHashMap.get(key);
        if (Objects.isNull(messageAction)) {
            log.error("[response 엑션 요청] action not found: {}", key);
            throw new IllegalArgumentException("[response 엑션 요청] action not found");
        }

        return messageAction;
    }

    public static RequestMessageAction getRequestMessageAction(String key) {
        RequestMessageAction messageAction = requestMessageActionMap.get(key);
        if (Objects.isNull(messageAction)) {
            log.error("[request 엑션 요청] action not found: {}", key);
            throw new IllegalArgumentException("[request 엑션 요청] action not found");
        }

        return messageAction;
    }
}
