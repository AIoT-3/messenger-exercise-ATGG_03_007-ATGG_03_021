package com.message.filter.impl;

import com.message.TypeManagement;
import com.message.config.ServerConfig;
import com.message.domain.ErrorManagement;
import com.message.domain.SessionManagement;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.exception.custom.BusinessException;
import com.message.exception.custom.filter.AlreadyAuthenticatedException;
import com.message.exception.custom.filter.UnauthenticatedException;
import com.message.filter.Filter;
import com.message.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class SessionFilter implements Filter {
    @Override
    public void doFilter(RequestDto request, FilterChain chain) {
        HeaderDto.RequestHeader header = request.header();
        String s = header.sessionId();

        boolean hasValidSession = (s != null && !s.trim().isEmpty() && SessionManagement.isExistedUuid(s));

        boolean isSkipTarget = ServerConfig.SkipSessionMethodNames.contains(header.type());

        if (isSkipTarget) {
            // 로그인 시도 -> 유효한 세션이 이미 있다면 -> 중복 로그인 차단
            if (hasValidSession) {
                log.debug("[세션 필터] 이미 로그인된 상태에서 접근 시도 - sessionId: {}", s);
                throw new AlreadyAuthenticatedException("이미 로그인된 상태입니다.");
            }
        } else {
            // 유효한 세션이 없으면 -> 접근 차단
            if (!hasValidSession) {
                log.debug("[세션 필터] 인증되지 않은 접근 - type: {}", header.type());

                // 로그아웃은 다른 에러코드로 응답
                if (header.type().equals(TypeManagement.Auth.LOGOUT)) {
                    throw new BusinessException(ErrorManagement.Auth.INVALID_SESSION, "유효하지 않은 세션입니다.", 401);
                }
                
                throw new UnauthenticatedException("로그인이 필요한 서비스입니다.");
            }
        }

        if (chain != null) {
            chain.doFilter(request);
        }
    }
}