package com.message.command;

import com.message.domain.MessageContent;
import com.message.subject.EventType;

public interface Command {
    String getType();
    EventType getEventType();
    Object execute(MessageContent.Message message);
}
