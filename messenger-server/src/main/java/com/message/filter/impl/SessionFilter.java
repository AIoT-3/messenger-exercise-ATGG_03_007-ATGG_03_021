package com.message.filter.impl;

import com.message.config.ServerConfig;
import com.message.domain.SessionManagement;
import com.message.dto.HeaderDto;
import com.message.exception.custom.filter.AlreadyAuthenticatedException;
import com.message.exception.custom.filter.UnauthenticatedException;
import com.message.filter.Filter;
import com.message.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class SessionFilter implements Filter {
    @Override
    public void doFilter(HeaderDto.RequestHeader header, FilterChain chain) {
        String s = header.sessionId();
        if (ServerConfig.SessionCheckMethodName.stream().anyMatch(name -> name.equals(header.type()))) {
            if (!Objects.isNull(s)) {
                log.debug("[세션 필터] 로그인 상태에서 로그인 시도 - userId:{}", SessionManagement.isExisted(s) ? SessionManagement.getUserId(s) : "존재하지 않은 세션(세션 삭제 요청)");
                throw new AlreadyAuthenticatedException("세션 필터] 로그인 상태에서 로그인 시도");
            }
        } else {
            if (Objects.isNull(s) || !SessionManagement.isExisted(s)) {
                log.debug("[세션 필터] 비로그인 상태에서 서비스 이용");
                throw new UnauthenticatedException("[세션 필터] 비로그인 상태에서 서비스 이용");
            }
        }
        if (!Objects.isNull(chain)) {
            chain.doFilter(header);
        }
    }
}