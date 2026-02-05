package com.message;

import com.message.dto.data.MessageDataType;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.ResponseDataDto;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TypeManagement {

    // --- 공통 및 에러 ---
    public static final String ERROR = "ERROR";

    // --- 로그인 관련 (Authentication) ---
    public static class Auth {
        public static final String LOGIN = "LOGIN";
        public static final String LOGIN_SUCCESS = "LOGIN-SUCCESS";
        
        public static final String LOGOUT = "LOGOUT";
        public static final String LOGOUT_SUCCESS = "LOGOUT-SUCCESS";
    }

    // --- 사용자 관련 (User) ---
    public static class User {
        public static final String LIST = "USER-LIST";
        public static final String LIST_SUCCESS = "USER-LIST-SUCCESS";
    }

    // --- 채팅 기능 (Chat & Room) ---
    public static class Chat {
        // 메시지 전송
        public static final String MESSAGE = "CHAT-MESSAGE";
        public static final String MESSAGE_SUCCESS = "CHAT-MESSAGE-SUCCESS";
        // 응답이랑 푸쉬 차이 주기 위해 만듬
        public static final String MESSAGE_RECEIVE = "CHAT-MESSAGE-RECEIVE";

        // 응답이랑 푸쉬 차이 주기 위해 만듬
        // 귓속말
        public static final String PRIVATE = "PRIVATE-MESSAGE";
        public static final String PRIVATE_SUCCESS = "PRIVATE-MESSAGE-SUCCESS";
        public static final String PRIVATE_MESSAGE_RECEIVE = "PRIVATE-MESSAGE-RECEIVE";
        
        // 메시지 기록
        public static final String HISTORY = "CHAT-MESSAGE-HISTORY";
        public static final String HISTORY_SUCCESS = "CHAT-MESSAGE-HISTORY-SUCCESS";
    }

    public static class Room {
        // 방 목록
        public static final String LIST = "CHAT-ROOM-LIST";
        public static final String LIST_SUCCESS = "CHAT-ROOM-LIST-SUCCESS";
        
        // 방 생성
        public static final String CREATE = "CHAT-ROOM-CREATE";
        public static final String CREATE_SUCCESS = "CHAT-ROOM-CREATE-SUCCESS";
        
        // 방 입장
        public static final String ENTER = "CHAT-ROOM-ENTER";
        public static final String ENTER_SUCCESS = "CHAT-ROOM-ENTER-SUCCESS";
        
        // 방 나가기
        public static final String EXIT = "CHAT-ROOM-EXIT";
        public static final String EXIT_SUCCESS = "CHAT-ROOM-EXIT-SUCCESS";
    }

    public static class Sync {
        public static final String USER = "SYNC-USER";
        public static final String ROOM = "SYNC-ROOM";
    }

    public static final Map<String, Class<? extends RequestDataDto>> requestDataDtoClassMap = new HashMap<>();
    public static final Map<String, Class<? extends ResponseDataDto>> responseDataDataDtoClassMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("com.message.dto.data.impl");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(MessageDataType.class);

        for (Class<?> clazz : annotatedClasses) {
            MessageDataType annotation = clazz.getAnnotation(MessageDataType.class);
            String type = annotation.value();

            if (RequestDataDto.class.isAssignableFrom(clazz)) {
                requestDataDtoClassMap.put(type, (Class<? extends RequestDataDto>) clazz);
            } else if (ResponseDataDto.class.isAssignableFrom(clazz)) {
                responseDataDataDtoClassMap.put(type, (Class<? extends ResponseDataDto>) clazz);
            }
        }
    }
}