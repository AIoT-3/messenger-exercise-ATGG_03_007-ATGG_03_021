package com.message.filter.impl;

import com.message.TypeManagement;
import com.message.domain.ErrorManagement;
import com.message.domain.SessionManagement;
import com.message.dto.RequestDto;
import com.message.dto.data.impl.AuthDto;
import com.message.exception.custom.BusinessException;
import com.message.exception.custom.filter.AlreadyAuthenticatedException;
import com.message.filter.Filter;
import com.message.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginStateCheckFilter implements Filter {
    @Override
    public void doFilter(RequestDto request, FilterChain chain) {

        if(request.header().type().equals(TypeManagement.Auth.LOGIN)) {
            boolean existed = SessionManagement.isExistedUserId(((AuthDto.LoginRequest) request.data()).userId());
            if (existed) {
                log.warn("[LoginStateCheckFilter] 이미 로그인된 사용자입니다 - userId: {}", ((AuthDto.LoginRequest) request.data()).userId());
                throw new AlreadyAuthenticatedException("[LoginStateCheckFilter] 이미 로그인된 사용자입니다.");
            }
        } else {
            String sessionId = request.header().sessionId();

            if(sessionId == null || !SessionManagement.isExistedUuid(sessionId)) {
                log.warn("[LoginStateCheckFilter] 인증되지 않은 접근 - sessionId: {}", sessionId);
                throw new BusinessException(ErrorManagement.Auth.UNAUTHORIZED, "권한이 없습니다.", 401);
            }
        }

        if (chain != null) {
            chain.doFilter(request);
        }
    }
}
