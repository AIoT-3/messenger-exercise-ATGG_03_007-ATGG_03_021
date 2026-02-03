package com.message.action;

import com.message.subject.EventType;

public interface MessageAction {
    String getMethod();
    EventType getEventType();
    void execute(Object arg);
}
