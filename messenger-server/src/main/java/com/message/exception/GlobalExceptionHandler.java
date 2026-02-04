package com.message.exception;

import com.message.dto.data.impl.ErrorDto;
import com.message.exception.custom.AlreadyExistException;
import com.message.exception.custom.BusinessException;
import com.message.exception.custom.InvalidRequestException;
import com.message.exception.custom.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalExceptionHandler {

    public ErrorDto exceptionHandler(Exception e) {

        if (e instanceof InvalidRequestException ire){
            return InvalidRequestExceptionHandler(ire);
        } else if (e instanceof NotFoundException nfe){
            return NotFoundExceptionHandler(nfe);
        } else if (e instanceof AlreadyExistException aee){
            return AlreadyExistExceptionHandler(aee);
        } else if (e instanceof BusinessException be) {
            return BusinessExceptionHandler(be);
        } else {
            return ExceptionHandler(e);
        }
    }

    private ErrorDto BusinessExceptionHandler(BusinessException be){
        log.warn("[Business Exception] httpStatus: {}, code: {}, message: {}", be.getHttpStatus(), be.getCode(), be.getMessage());
        return createErrorResponse(be.getCode(), be.getMessage());
    }

    private ErrorDto InvalidRequestExceptionHandler(InvalidRequestException ire){
        log.warn("[Bad Request] httpStatus: {}, code: {}, message: {}", ire.getHttpStatus(), ire.getCode(), ire.getMessage());
        return createErrorResponse(ire.getCode(), ire.getMessage());
    }

    private ErrorDto NotFoundExceptionHandler(NotFoundException nfe){
        log.warn("[Resource Not Found] httpStatus: {}, code: {}, message: {}", nfe.getHttpStatus(), nfe.getCode(), nfe.getMessage());
        return createErrorResponse(nfe.getCode(), nfe.getMessage());
    }

    private ErrorDto AlreadyExistExceptionHandler(AlreadyExistException aee){
        log.warn("[Already Existed Resource] httpStatus: {}, code: {}, message: {}", aee.getHttpStatus(), aee.getCode(), aee.getMessage());
        return createErrorResponse(aee.getCode(), aee.getMessage());
    }

    private ErrorDto ExceptionHandler(Exception e){
        log.error("알 수 없는 예외 터짐 - message: {}", e.getMessage());
        return createErrorResponse("Error","알 수 없는 예외 터짐");
    }

    private ErrorDto createErrorResponse(String code, String message){
        return new ErrorDto(code, message);
    }

}