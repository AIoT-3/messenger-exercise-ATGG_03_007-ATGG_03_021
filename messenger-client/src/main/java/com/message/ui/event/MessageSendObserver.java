package com.message.ui.event;

public class MessageSendObserver implements Observer {
    private final MessageAction messageAction;

    public MessageSendObserver(MessageAction messageAction) {
        this.messageAction = messageAction;
    }

    @Override
    public void update(Object arg) {
        messageAction.execute(arg);
    }
}
