package com.message.observer;

import com.message.subject.EventType;

public interface Observer {
    EventType getEventType();
    void updateMessage(Object message);
    default boolean validate(EventType eventType){
        return getEventType().equals(eventType);
    }
}
