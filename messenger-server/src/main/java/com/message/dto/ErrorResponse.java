package com.message.dto;

public record ErrorResponse(
        String code,
        String message
    ) {}