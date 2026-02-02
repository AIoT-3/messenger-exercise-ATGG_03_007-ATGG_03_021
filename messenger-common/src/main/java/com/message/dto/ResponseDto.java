package com.message.dto;

import com.message.dto.data.ResponseDataDto;

public record ResponseDto(
        HeaderDto.ResponseHeader header,
        ResponseDataDto data
) {
}
