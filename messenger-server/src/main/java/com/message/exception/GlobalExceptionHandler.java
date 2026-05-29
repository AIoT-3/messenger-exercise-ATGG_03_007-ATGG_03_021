package com.message.exception;

import com.message.dto.data.impl.ErrorDto;
import com.message.exception.custom.AlreadyExistException;
import com.message.exception.custom.BusinessException;
import com.message.exception.custom.InvalidRequestException;
import com.message.exception.custom.NotFoundException;
import com.message.exception.custom.filter.AlreadyAuthenticatedException;
import com.message.exception.custom.filter.UnauthenticatedException;
import com.message.exception.custom.handler.HandlerNotFoundException;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalExceptionHandler {

    public ErrorDto exceptionHandler(Exception e) {
        // 구체적인 예외를 먼저, 부모 예외를 나중에 체크 (상속 순서 고려)
        if (e instanceof AlreadyAuthenticatedException aae) {
            return handleBadRequest(aae.getCode(), aae.getMessage());
        } else if (e instanceof UnauthenticatedException ue) {
            return handleBadRequest(ue.getCode(), ue.getMessage());
        } else if (e instanceof InvalidRequestException ire) {
            log.warn("[InvalidRequestException] status={}, code={}, msg={}", ire.getHttpStatus(), ire.getCode(), ire.getMessage());
            return createErrorResponse(ire.getCode(), ire.getMessage());
        } else if (e instanceof HandlerNotFoundException hne) {
            return handleNotFound(hne.getCode(), hne.getMessage());
        } else if (e instanceof NotFoundException nfe) {
            log.warn("[NotFoundException] status={}, code={}, msg={}", nfe.getHttpStatus(), nfe.getCode(), nfe.getMessage());
            return createErrorResponse(nfe.getCode(), nfe.getMessage());
        } else if (e instanceof AlreadyExistException aee) {
            log.warn("[AlreadyExistException] status={}, code={}, msg={}", aee.getHttpStatus(), aee.getCode(), aee.getMessage());
            return createErrorResponse(aee.getCode(), aee.getMessage());
        } else if (e instanceof ObjectMappingFailException omfe) {
            return handleBadRequest(omfe.getCode(), omfe.getMessage());
        } else if (e instanceof BusinessException be) {
            log.warn("[BusinessException] status={}, code={}, msg={}", be.getHttpStatus(), be.getCode(), be.getMessage());
            return createErrorResponse(be.getCode(), be.getMessage());
        } else {
            return handleUnknown(e);
        }
    }

    private ErrorDto handleBadRequest(String code, String message) {
        log.warn("[BadRequest] code={}, msg={}", code, message);
        return createErrorResponse(code, message);
    }

    private ErrorDto handleNotFound(String code, String message) {
        log.warn("[NotFound] code={}, msg={}", code, message);
        return createErrorResponse(code, message);
    }

    private ErrorDto handleUnknown(Exception e) {
        log.error("[UnknownException] 처리되지 않은 예외 - type={}, msg={}", e.getClass().getSimpleName(), e.getMessage(), e);
        return createErrorResponse("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.");
    }

    private ErrorDto createErrorResponse(String code, String message) {
        return new ErrorDto(code, message);
    }
}
