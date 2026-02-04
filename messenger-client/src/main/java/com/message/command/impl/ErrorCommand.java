package com.message.command.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.TypeManagement;
import com.message.command.ReceiveCommand;
import com.message.domain.MessageContent;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 에러 응답 처리 Command
 */
public class ErrorCommand extends ReceiveCommand {
    private static final Logger log = LoggerFactory.getLogger(ErrorCommand.class);

    @Override
    public String getType() {
        return TypeManagement.ERROR;
    }

    @Override
    public Object execute(MessageContent.Message message) {
        MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

        try {
            // Header 파싱
            HeaderDto.ResponseHeader header = mapper.treeToValue(
                responseMessage.header(),
                HeaderDto.ResponseHeader.class
            );

            // Data 파싱
            ErrorDto data = mapper.treeToValue(
                responseMessage.data(),
                ErrorDto.class
            );

            log.error("서버 에러 수신 - code: {}, message: {}", data.code(), data.message());

            return new ResponseDto(header, data);
        } catch (JsonProcessingException e) {
            log.error("에러 응답 파싱 실패", e);
            throw new RuntimeException("에러 응답 처리 실패", e);
        }
    }
}