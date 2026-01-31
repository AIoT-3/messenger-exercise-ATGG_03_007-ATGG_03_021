package com.message.dto;

import java.time.OffsetDateTime;

public class HeaderDto {
    public record RequestHeader(
            String type,
            OffsetDateTime timestamp,
            String sessionId
    ){}

    public record ResponseHeader(
            String type,
            boolean success,
            OffsetDateTime timestamp
    ){}
}
