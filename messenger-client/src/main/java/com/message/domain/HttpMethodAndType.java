package com.message.domain;

public record HttpMethodAndType (
        HttpMethod httpMethod,
        String type
) {
}
