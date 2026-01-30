package com.message.dto;

public class AuthDto {
    public record LoginRequest(
            String userId,
            String password
    ) {}
    
    public record LoginResponse(
        String userId, 
        String sessionId, 
        String message
    ) {}

    public record LogoutResponse(
            String message
    ) {}
}