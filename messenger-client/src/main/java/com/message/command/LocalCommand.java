package com.message.command;

import com.message.subject.EventType;

public interface LocalCommand extends Command{

    @Override
    default EventType getEventType(){
        return EventType.LOCAL;
    }
}
