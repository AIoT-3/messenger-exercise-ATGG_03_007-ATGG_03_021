package com.message.handler;

import com.message.exception.custom.handler.HandlerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.*;

/**
 * 메시지 핸들러 팩토리 클래스입니다.
 * <p>
 * 이 클래스는 {@link Handler} 인터페이스를 구현한 모든 핸들러 클래스를 검색하여 초기화하고,
 * 요청된 메서드 이름에 해당하는 핸들러 인스턴스를 제공하는 역할을 합니다.
 * </p>
 */
@Slf4j
public class HandlerFactory {
    /**
     * 메서드 이름을 키로 하고, 해당 메서드를 처리하는 {@link Handler} 인스턴스를 값으로 가지는 맵입니다.
     */
    private static final Map<String, Handler> messageHandlerMap = new HashMap<>();

    static {
        // "com.message.handler" 패키지 내의 MessageHandler 하위 타입들을 검색합니다.
        Reflections reflections = new Reflections("com.message.handler");
        Set<Class<? extends Handler>> classes = reflections.getSubTypesOf(Handler.class);

        for (Class<? extends Handler> clazz : classes) {
            try {
                // 각 핸들러 클래스의 인스턴스를 생성합니다.
                Handler handler = clazz.getDeclaredConstructor().newInstance();
                log.debug("messageHandler-factory init :  instance :{}", handler.getClass().getName());
                // 핸들러의 메서드 이름을 키로 하여 맵에 등록합니다.
                messageHandlerMap.put(handler.getMethod(), handler);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    /**
     * 주어진 메서드 이름에 해당하는 {@link Handler}를 반환합니다.
     *
     * @param method 핸들러를 찾기 위한 메서드 이름
     * @return 해당 메서드를 처리하는 {@link Handler} 인스턴스
     * @throws HandlerNotFoundException 해당 메서드에 대한 핸들러가 존재하지 않을 경우 발생
     */
    public static Handler getMessageHandler(String method) {
        Handler handler = messageHandlerMap.get(method);
        if (Objects.isNull(handler)) {
            log.error("[핸들러 요청] handler not found: {}", method);
            throw new HandlerNotFoundException("[핸들러 요청] 없는 거 찾지마");
        }

        return handler;
    }

    public static List<String> getMethods() {
        return messageHandlerMap.keySet().stream().toList();
    }
}
