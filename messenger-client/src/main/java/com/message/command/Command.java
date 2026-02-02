package com.message.command;

import com.message.domain.HttpMethodAndType;
import com.message.dto.RequestDto;
import com.message.dto.ResponseDto;

public interface Command {
    HttpMethodAndType getHttpMethodAndType();
    String execute(RequestDto request, ResponseDto response);
}
