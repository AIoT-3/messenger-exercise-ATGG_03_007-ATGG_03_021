package com.message.subject;

import com.message.command.Command;
import com.message.command.CommandFactory;
import com.message.domain.MessageContent;
import com.message.observer.Observer;
import com.message.route.MessageRoute;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessageSubject implements Subject {

    private final Map<EventType, Observer> observers = new ConcurrentHashMap<>();

    @Override
    public void register(EventType eventType, Observer observer) {
        observers.put(eventType, observer);
    }

    @Override
    public void remove(EventType eventType, Observer observer) {
        observers.remove(eventType, observer);
    }

    @Override
    public void notifyObservers(EventType eventType, String message) {
        MessageContent.Message messageContent = MessageRoute.getMessageContent(eventType, message);
        Command command = CommandFactory.getCommand(eventType, messageContent.getType());
        Object result = command.execute(messageContent);
        if(result instanceof String str && str.contains("redirect:")){
            //TODO local 요청 ui처리 - 예: 로그인 창 띄우기
        } else {
            observers.get(eventType).updateMessage(result);
        }
    }
}
