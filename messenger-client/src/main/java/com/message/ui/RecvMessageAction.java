package com.message.ui;

import com.message.TypeManagement;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.AuthDto;
import com.message.dto.data.impl.ChatDto;
import com.message.dto.data.impl.ErrorDto;
import com.message.session.ClientSession;
import com.message.ui.event.MessageAction;

public class RecvMessageAction implements MessageAction {
    private final MessageClientForm form;

    public RecvMessageAction(MessageClientForm form) {
        this.form = form;
    }

    @Override
    public void execute(Object arg) {
        if (arg instanceof ResponseDto response) {
            handleResponse(response);
        } else if (arg instanceof String message) {
            form.getMessageArea().append(message + "\n");
        }
    }

    private void handleResponse(ResponseDto response) {
        if (response.header().success()) {
            String type = response.header().type();
            
            if (TypeManagement.Auth.LOGIN_SUCCESS.equals(type)) {
                if (response.data() instanceof AuthDto.LoginResponse loginResponse) {
                    ClientSession.setSessionId(loginResponse.sessionId());
                    ClientSession.setUserId(loginResponse.userId());
                    form.getMessageArea().append("[System] Login Successful: " + loginResponse.message() + "\n");
                }
            } else if (TypeManagement.Auth.LOGOUT_SUCCESS.equals(type)) {
                ClientSession.clear();
                form.getMessageArea().append("[System] Logout Successful\n");
            } else if (TypeManagement.Chat.MESSAGE_SUCCESS.equals(type)) {
                if (response.data() instanceof ChatDto.MessageResponse messageResponse){
                    form.getMessageArea();
                }

            } else if (TypeManagement.Chat.MESSAGE.equals(type)) {
                if (response.data() instanceof ChatDto.MessageResponse messageResponse) {
                     form.getMessageArea().append(messageResponse.messageId() + "\n");
                }
            }
        } else {
            if (response.data() instanceof ErrorDto errorDto) {
                form.getMessageArea().append("[Error] " + errorDto.message() + "\n");
            } else {
                form.getMessageArea().append("[Error] Unknown error\n");
            }
        }
    }
}
