package com.message.ui.event;

public class MessageRecvObserver implements Observer {
    private final MessageAction messageAction;

    public MessageRecvObserver(MessageAction messageAction) {
        this.messageAction = messageAction;
    }

    @Override
    public void update(Object arg) {
        messageAction.execute(arg);
    }
}
