package com.message.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.message.subject.EventType;

public abstract class ReceiveCommand implements Command{
    protected final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public EventType getEventType(){
        return EventType.RECV;
    }
}
