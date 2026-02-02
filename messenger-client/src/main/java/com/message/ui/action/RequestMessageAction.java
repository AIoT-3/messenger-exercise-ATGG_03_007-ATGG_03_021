package com.message.ui.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.session.ClientSession;
import com.message.ui.event.EventType;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
public abstract class RequestMessageAction implements MessageAction{
    protected final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public EventType getEventType(){
        return EventType.SEND;
    }

    @Override
    public void execute(Object arg){
        if (!(arg instanceof String text)) {
            return;
        }

        try {
            RequestDto requestDto = createRequest(text);
            if (requestDto != null) {
                ClientSession.send(requestDto);
            }
        } catch (Exception e) {
            log.error("Failed to send message", e);
        }
    }

    protected abstract RequestDto createRequest(String text);

    protected HeaderDto.RequestHeader createRequestHeader(String type) {
        return new HeaderDto.RequestHeader(type, OffsetDateTime.now(), ClientSession.getSessionId());
    }
}
