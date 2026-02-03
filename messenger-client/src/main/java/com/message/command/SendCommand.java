package com.message.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.message.dto.HeaderDto;
import com.message.session.ClientSession;
import com.message.subject.EventType;

import java.time.OffsetDateTime;

public abstract class SendCommand implements Command{
    protected final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public EventType getEventType(){
        return EventType.SEND;
    }

    protected HeaderDto.RequestHeader createRequestHeader(String type){
        return new HeaderDto.RequestHeader(
                type,
                OffsetDateTime.now(),
                ClientSession.getSessionId()
        );
    }
}
