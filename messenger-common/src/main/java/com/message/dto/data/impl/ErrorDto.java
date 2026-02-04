package com.message.dto.data.impl;

import com.message.TypeManagement;
import com.message.dto.data.MessageDataType;
import com.message.dto.data.ResponseDataDto;

@MessageDataType(TypeManagement.ERROR)
public record ErrorDto(
        String code,
        String message
) implements ResponseDataDto {
    @Override
    public String getType() {
        return TypeManagement.ERROR;
    }
}