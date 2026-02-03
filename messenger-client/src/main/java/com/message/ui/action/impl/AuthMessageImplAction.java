package com.message.ui.action.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.message.TypeManagement;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.AuthDto;
import com.message.ui.MessageClientForm;
import com.message.ui.action.RequestMessageAction;
import com.message.ui.action.ResponseMessageAction;

public class AuthMessageImplAction {
    public class LoginRequestMessageAction extends RequestMessageAction {
        private static final String METHOD = TypeManagement.Auth.LOGIN;

        @Override
        protected RequestDto createRequest(String text) {
            try {
                JsonNode jsonNode = mapper.readTree(text);
                String userId = jsonNode.path("userId").asText();
                String password = jsonNode.path("password").asText();
                AuthDto.LoginRequest loginRequest = new AuthDto.LoginRequest(userId, password);
                HeaderDto.RequestHeader requestHeader = createRequestHeader(METHOD);

                return new RequestDto(requestHeader, loginRequest);
            } catch (JsonProcessingException e) {
                throw new RuntimeException();
            }
        }

        @Override
        public String getMethod() {
            return METHOD;
        }
    }

    public class LoginResponseMessageAction extends ResponseMessageAction {
        private final static String METHOD = TypeManagement.Auth.LOGIN;

        public LoginResponseMessageAction(MessageClientForm form) {
            super(form);
        }

        @Override
        protected void handleResponse(ResponseDto response) {

        }

        @Override
        public String getMethod() {
            return METHOD;
        }
    }

    public class LogoutRequestMessageAction extends RequestMessageAction {
        private final static String METHOD = TypeManagement.Auth.LOGIN;

        @Override
        protected RequestDto createRequest(String text) {
            HeaderDto.RequestHeader requestHeader = createRequestHeader(METHOD);

            return new RequestDto(requestHeader, null);
        }

        @Override
        public String getMethod() {
            return METHOD;
        }
    }

    public class LogoutResponseMessageAction extends ResponseMessageAction {
        private final static String METHOD = TypeManagement.Auth.LOGIN;

        public LogoutResponseMessageAction(MessageClientForm form) {
            super(form);
        }

        @Override
        protected void handleResponse(ResponseDto response) {

        }

        @Override
        public String getMethod() {
            return METHOD;
        }
    }
}
