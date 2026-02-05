package com.message.observer.impl;

import com.message.action.MessageAction;
import com.message.observer.Observer;
import com.message.subject.EventType;

public class MessageSendObserver implements Observer {
    private final MessageAction messageAction;

    public MessageSendObserver(MessageAction messageAction) {
        this.messageAction = messageAction;
    }

    @Override
    public EventType getEventType() {
        return EventType.SEND;
    }

    @Override
    public void updateMessage(Object message) {
        messageAction.execute(message);
    }

}
