package com.message.handler;

import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;

public interface Handler {
    String getMethod();
    Object execute(HeaderDto.RequestHeader header, RequestDataDto data);

    default boolean validate(String method){
        return getMethod().equals(method);
    }
}