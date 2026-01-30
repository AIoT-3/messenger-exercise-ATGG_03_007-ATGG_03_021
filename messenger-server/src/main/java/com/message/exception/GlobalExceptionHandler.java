package com.message.exception;

import com.message.dto.ErrorResponse;
import com.message.exception.custom.AlreadyExistException;
import com.message.exception.custom.InvalidRequestException;
import com.message.exception.custom.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalExceptionHandler {

    public ErrorResponse exceptionHandler(Exception e) {

        if (e instanceof InvalidRequestException ire){
            return InvalidRequestExceptionHandler(ire);
        } else if (e instanceof NotFoundException nfe){
            return NotFoundExceptionHandler(nfe);
        } else if (e instanceof AlreadyExistException aee){
            return AlreadyExistExceptionHandler(aee);
        } else {
            return ExceptionHandler(e);
        }
    }

    private ErrorResponse InvalidRequestExceptionHandler(InvalidRequestException ire){
        log.warn("[Bad Request] httpStatus: {}, code: {}, message: {}", ire.getHttpStatus(), ire.getCode(), ire.getMessage());
        return createErrorResponse(ire.getCode(), ire.getMessage());
    }

    private ErrorResponse NotFoundExceptionHandler(NotFoundException nfe){
        log.warn("[Resource Not Found] httpStatus: {}, code: {}, message: {}", nfe.getHttpStatus(), nfe.getCode(), nfe.getMessage());
        return createErrorResponse(nfe.getCode(), nfe.getMessage());
    }

    private ErrorResponse AlreadyExistExceptionHandler(AlreadyExistException aee){
        log.warn("[Already Existed Resource] httpStatus: {}, code: {}, message: {}", aee.getHttpStatus(), aee.getCode(), aee.getMessage());
        return createErrorResponse(aee.getCode(), aee.getMessage());
    }

    private ErrorResponse ExceptionHandler(Exception e){
        log.error("알 수 없는 예외 터짐 - message: {}", e.getMessage());
        return createErrorResponse("Error","알 수 없는 예외 터짐");
    }

    private ErrorResponse createErrorResponse(String code, String message){
        return new ErrorResponse(code, message);
    }

}