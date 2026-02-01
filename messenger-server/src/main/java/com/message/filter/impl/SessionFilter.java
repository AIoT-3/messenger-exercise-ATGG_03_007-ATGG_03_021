package com.message.filter.impl;

import com.message.config.AppConfig;
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

        // TODO 수정사항 (재민)
        // 세션 존재 여부 확인 -> null이 아니면서 비어있지 않고 서버 메모리에도 등록되어 있어야 함
        boolean hasValidSession = (s != null && !s.trim().isEmpty() && SessionManagement.isExisted(s));

        // TODO 수정사항 (재민)
        // 인증이 필요한 요청인지(비로그인 허용 대상인지 확인) -> 조건문 가독성 업
        boolean isSkipTarget = AppConfig.SkipSessionMethodNames.contains(header.type());

        if (isSkipTarget) {
            // 로그인 시도 -> 유효한 세션이 이미 있다면 -> 중복 로그인 차단
            if (hasValidSession) {
                log.debug("[세션 필터] 이미 로그인된 상태에서 접근 시도 - sessionId: {}", s);
                throw new AlreadyAuthenticatedException("이미 로그인된 상태입니다.");
            }
            // 세션이 없으면 통과 -> 로그인 하러 감
        } else {
            // 유효한 세션이 없으면 -> 접근 차단
            if (!hasValidSession) {
                log.debug("[세션 필터] 인증되지 않은 접근 - type: {}", header.type());
                throw new UnauthenticatedException("로그인이 필요한 서비스입니다.");
            }
        }

//        if (AppConfig.SessionCheckMethodName.stream().anyMatch(name -> name.equals(header.type()))) {
//            // 로그인 시도
//            if(hasSession) {
//                log.debug("[세션 필터] 로그인 상태에서 로그인 시도 - userId: {}", SessionManagement.getUserId(s));
//                throw new AlreadyAuthenticatedException("[세션 필터] 이미 로그인된 상태에서 로그인 시도");
//            }
////            if (!Objects.isNull(s)) {
////                log.debug("[세션 필터] 로그인 상태에서 로그인 시도 - userId:{}", SessionManagement.isExisted(s) ? SessionManagement.getUserId(s) : "존재하지 않은 세션(세션 삭제 요청)");
////                throw new AlreadyAuthenticatedException("세션 필터] 로그인 상태에서 로그인 시도");
////            }
//        } else {
//            // 일반 서비스 요청일 때
//            if(!hasSession || !SessionManagement.isExisted(s)) {
//                log.debug("[세션 필터] 비로그인 상태에서 서비스 이용");
//                throw new UnauthenticatedException("[세션 필터] 비로그인 상태에서 서비스 이용");
//            }
////            if (Objects.isNull(s) || !SessionManagement.isExisted(s)) {
////                log.debug("[세션 필터] 비로그인 상태에서 서비스 이용");
////                throw new UnauthenticatedException("[세션 필터] 비로그인 상태에서 서비스 이용");
////            }
//        }

        // 필터 통과 후 다음 단계로 진행
        if (Objects.nonNull(chain)) {
            chain.doFilter(header);
        }
    }
}