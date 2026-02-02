package com.message.command;

import com.message.domain.HttpMethodAndType;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.*;

@Slf4j
public class CommandFactory {
    private final static Map<HttpMethodAndType, Command> commandMap = new HashMap<>();

    static {
        // "com.message.command" 패키지 내의 Command 하위 타입들을 검색합니다.
        Reflections reflections = new Reflections("com.message.command");
        Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> clazz : classes) {
            try {
                // 각 핸들러 클래스의 인스턴스를 생성합니다.
                Command command = clazz.getDeclaredConstructor().newInstance();
                log.debug("[CommandFactory] CommandFactory 초기화 - instance: {}", command.getClass().getName());
                // 핸들러의 메서드 이름을 키로 하여 맵에 등록합니다.
                commandMap.put(command.getHttpMethodAndType(), command);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public static Command getMessageHandler(HttpMethodAndType key) {
        Command command = commandMap.get(key);
        if (Objects.isNull(command)) {
            log.error("[커멘드 요청] handler not found: {}", key);
            throw new IllegalArgumentException("[커맨드 요청] 찾지마");
        }

        return command;
    }

    public static List<HttpMethodAndType> getHttpMethodAndType() {
        return commandMap.keySet().stream().toList();
    }
}
