package com.message.observer.impl;

import com.message.action.MessageAction;
import com.message.observer.Observer;
import com.message.subject.EventType;

public class MessageRecvObserver implements Observer {
    private final MessageAction messageAction;

    public MessageRecvObserver(MessageAction messageAction) {
        this.messageAction = messageAction;
    }

    @Override
    public EventType getEventType() {
        return EventType.RECV;
    }

    @Override
    public void updateMessage(Object message) {
        messageAction.execute(message);
    }

}
