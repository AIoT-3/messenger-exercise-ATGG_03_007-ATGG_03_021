package com.message.dto;

import com.message.dto.data.RequestDataDto;

public record RequestDto(
        HeaderDto.RequestHeader header,
        RequestDataDto data
) {
}
