package com.message.command;

import com.message.subject.EventType;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommandFactory {
    private static final Logger log = LoggerFactory.getLogger(CommandFactory.class);
    private final static Map<String, Command> sendCommandMap = new ConcurrentHashMap<>();
    private final static Map<String, Command> receiveCommandMap = new ConcurrentHashMap<>();
    private final static Map<String, Command> localCommandMap = new ConcurrentHashMap<>();

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
                if(command.getEventType().equals(EventType.SEND)){
                    sendCommandMap.put(command.getType(), command);
                } else if(command.getEventType().equals(EventType.RECV)){
                    receiveCommandMap.put(command.getType(), command);
                } else if(command.getEventType().equals(EventType.LOCAL)){
                    localCommandMap.put(command.getType(), command);
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public static Command getCommand(EventType eventType, String type) {
        Command command;
        if(eventType.equals(EventType.SEND)){
            command = sendCommandMap.get(type);
        } else if(eventType.equals(EventType.RECV)){
            command = receiveCommandMap.get(type);
        } else if(eventType.equals(EventType.LOCAL)){
            command = localCommandMap.get(type);
        } else {
            throw new IllegalArgumentException();
        }
        return command;
    }

}
