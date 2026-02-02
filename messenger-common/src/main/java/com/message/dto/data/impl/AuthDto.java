package com.message.dto.data.impl;

import com.message.TypeManagement;
import com.message.dto.data.MessageDataType;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.ResponseDataDto;

public class AuthDto {

    @MessageDataType(TypeManagement.Auth.LOGIN)
    public record LoginRequest(
            String userId,
            String password
    ) implements RequestDataDto {
        @Override
        public String getType() {
            return TypeManagement.Auth.LOGIN;
        }
    }

    @MessageDataType(TypeManagement.Auth.LOGIN_SUCCESS)
    public record LoginResponse(
            String userId,
            String sessionId,
            String message
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Auth.LOGIN_SUCCESS;
        }
    }

    @MessageDataType(TypeManagement.Auth.LOGOUT_SUCCESS)
    public record LogoutResponse(
            String message
    ) implements ResponseDataDto {
        @Override
        public String getType() {
            return TypeManagement.Auth.LOGOUT_SUCCESS;
        }
    }
}