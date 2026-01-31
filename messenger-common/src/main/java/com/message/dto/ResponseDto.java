package com.message.dto;

public record ResponseDto<T>(
        HeaderDto.ResponseHeader header,
        T data
) {
}
