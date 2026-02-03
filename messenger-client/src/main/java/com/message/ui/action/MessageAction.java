package com.message.ui.action;

import com.message.ui.event.EventType;

public interface MessageAction {
    String getMethod();
    EventType getEventType();
    void execute(Object arg);
}
