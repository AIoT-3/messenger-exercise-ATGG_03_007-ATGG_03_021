package com.message.command.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.TypeManagement;
import com.message.command.ReceiveCommand;
import com.message.command.SendCommand;
import com.message.domain.MessageContent;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.AuthDto;
import com.message.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 인증 관련 Command 클래스들
 * 로그인/로그아웃 요청 및 응답 처리
 */
public class AuthCommand {

    /**
     * 로그인 요청 Command
     * 사용자 입력을 JSON 형식으로 변환하여 서버로 전송
     */
    public static class LoginSendCommand extends SendCommand {
        private static final Logger log = LoggerFactory.getLogger(LoginSendCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Auth.LOGIN;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.RequestMessage requestMessage = (MessageContent.RequestMessage) message;
            String content = requestMessage.content();

            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("로그인 정보가 올바르지 않습니다.");
            }

            String[] contents = content.trim().split("\\s+", 2);

            if (contents.length != 2) {
                throw new IllegalArgumentException("사용자 ID와 비밀번호를 모두 입력해주세요.");
            }

            String userId = contents[0].trim();
            String password = contents[1].trim();

            HeaderDto.RequestHeader header = createRequestHeader(requestMessage.type());
            AuthDto.LoginRequest data = new AuthDto.LoginRequest(userId, password);

            try {
                String json = mapper.writeValueAsString(new RequestDto(header, data));
                log.debug("로그인 요청 JSON 생성: {}", json);
                return json;
            } catch (JsonProcessingException e) {
                log.error("로그인 요청 JSON 변환 실패", e);
                throw new RuntimeException("로그인 요청 생성 실패", e);
            }
        }
    }

    /**
     * 로그인 성공 응답 Command
     * 서버로부터 받은 로그인 성공 응답을 DTO로 변환
     */
    public static class LoginReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(LoginReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Auth.LOGIN_SUCCESS;
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
                AuthDto.LoginResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    AuthDto.LoginResponse.class
                );

                // 세션 정보 저장
                ClientSession.setSessionId(data.sessionId());
                ClientSession.setUserId(data.userId());

                log.info("로그인 성공 - userId: {}, sessionId: {}", data.userId(), data.sessionId());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("로그인 응답 파싱 실패", e);
                throw new RuntimeException("로그인 응답 처리 실패", e);
            }
        }
    }

    /**
     * 로그아웃 요청 Command
     * 로그아웃 요청을 JSON 형식으로 변환하여 서버로 전송
     */
    public static class LogoutSendCommand extends SendCommand {
        private static final Logger log = LoggerFactory.getLogger(LogoutSendCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Auth.LOGOUT;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            HeaderDto.RequestHeader header = createRequestHeader(TypeManagement.Auth.LOGOUT);

            try {
                // 로그아웃은 data가 필요 없음
                String json = mapper.writeValueAsString(new RequestDto(header, null));
                log.debug("로그아웃 요청 JSON 생성: {}", json);
                return json;
            } catch (JsonProcessingException e) {
                log.error("로그아웃 요청 JSON 변환 실패", e);
                throw new RuntimeException("로그아웃 요청 생성 실패", e);
            }
        }
    }

    /**
     * 로그아웃 성공 응답 Command
     * 서버로부터 받은 로그아웃 성공 응답을 DTO로 변환
     */
    public static class LogoutReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(LogoutReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Auth.LOGOUT_SUCCESS;
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
                AuthDto.LogoutResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    AuthDto.LogoutResponse.class
                );

                // 세션 정보 초기화
                ClientSession.clear();

                log.info("로그아웃 성공 - message: {}", data.message());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("로그아웃 응답 파싱 실패", e);
                throw new RuntimeException("로그아웃 응답 처리 실패", e);
            }
        }
    }
}