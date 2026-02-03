package com.message.command.impl;

import com.message.TypeManagement;
import com.message.command.Command;
import com.message.domain.HttpMethod;
import com.message.domain.HttpMethodAndType;
import com.message.dto.RequestDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.AuthDto;
import com.message.dto.data.impl.ErrorDto;
import com.message.session.ClientSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthCommandImpl {

    public static class GetLogin implements Command {
        private final static HttpMethodAndType httpMethodAndType = new HttpMethodAndType(HttpMethod.GET, TypeManagement.Auth.LOGIN);

        @Override
        public HttpMethodAndType getHttpMethodAndType() {
            return httpMethodAndType;
        }

        @Override
        public String execute(RequestDto request, ResponseDto response) {
            if (ClientSession.isAuthenticated()) {
                log.debug("[로그인 요청] 이미 로그인 상태입니다 - userId:{}", ClientSession.getUserId());
                return "이미 로그인 상태입니다.";
            }
            return "";
        }
    }

    public static class PostLogin implements Command {
        private final static HttpMethodAndType httpMethodAndType = new HttpMethodAndType(HttpMethod.POST, TypeManagement.Auth.LOGIN);

        @Override
        public HttpMethodAndType getHttpMethodAndType() {
            return httpMethodAndType;
        }

        @Override
        public String execute(RequestDto request, ResponseDto response) {
            if (ClientSession.isAuthenticated()) {
                log.debug("[로그인 요청] 이미 로그인 상태입니다 - userId:{}", ClientSession.getUserId());
                return "이미 로그인 상태입니다.";
            }

            ClientSession.send(request);
            return "로그인 요청 전송됨";
        }
    }

    public static class doPostLogout implements Command {
        private final static HttpMethodAndType httpMethodAndType = new HttpMethodAndType(HttpMethod.POST, TypeManagement.Auth.LOGOUT);

        @Override
        public HttpMethodAndType getHttpMethodAndType() {
            return httpMethodAndType;
        }

        @Override
        public String execute(RequestDto request, ResponseDto response) {
            if (!ClientSession.isAuthenticated()) {
                log.debug("[로그아웃 요청] 잘못된 로그아웃 요청입니다.");
                return "로그인 상태가 아닙니다.";
            }

            ClientSession.send(request);
            return "로그아웃 요청 전송됨";
        }
    }
}