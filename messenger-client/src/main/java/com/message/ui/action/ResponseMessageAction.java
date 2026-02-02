package com.message.ui.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.message.dto.ResponseDto;
import com.message.ui.MessageClientForm;
import com.message.ui.event.EventType;

public abstract class ResponseMessageAction implements MessageAction {
    protected final MessageClientForm form;
    protected final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    protected ResponseMessageAction(MessageClientForm form){
        this.form = form;
    }

    @Override
    public EventType getEventType(){
       return EventType.RECV;
    }

    @Override
    public void execute(Object arg) {
        if (arg instanceof ResponseDto response) {
            handleResponse(response);
        } else if (arg instanceof String message) {
            form.getMessageArea().append(message + "\n");
        }
    }

    protected abstract void handleResponse(ResponseDto response);
}
