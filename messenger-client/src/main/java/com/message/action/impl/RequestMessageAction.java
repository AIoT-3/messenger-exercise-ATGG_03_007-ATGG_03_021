package com.message.action.impl;

import com.message.action.MessageAction;
import com.message.cofig.AppConfig;
import com.message.session.ClientSession;
import com.message.subject.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 요청 메시지 Action
 * Command에서 생성된 JSON 문자열을 서버로 전송
 */
public class RequestMessageAction implements MessageAction {
    private static final Logger log = LoggerFactory.getLogger(RequestMessageAction.class);

    @Override
    public String getMethod() {
        return "SEND";
    }

    @Override
    public EventType getEventType() {
        return EventType.SEND;
    }

    public void execute(Object arg) {
        if (arg instanceof String jsonMessage) {
            sendToServer(jsonMessage);
        } else {
            log.warn("잘못된 메시지 형식: {}", arg != null ? arg.getClass().getName() : "null");
        }
    }

    private void sendToServer(String jsonMessage) {
        try {
            // ClientSession에서 OutputStream을 가져옵니다. (Socket.getOutputStream())
            OutputStream out = ClientSession.getOutputStream();

            if (out != null) {
                // 1. JSON을 UTF-8 바이트 배열로 변환 (중요: 길이는 여기서 구해야 함)
                byte[] bodyBytes = jsonMessage.getBytes(StandardCharsets.UTF_8);

                // 2. 헤더 생성 (끝에 \n을 붙여 서버의 readLine()과 호환)
                String header = AppConfig.MESSAGE_LENGTH + bodyBytes.length + "\n";
                byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);

                // 3. 전송
                out.write(headerBytes); // 헤더 전송
                out.write(bodyBytes);   // 본문 바이트 전송
                out.flush();

                log.debug("서버로 메시지 전송 성공 - Byte length: {}, message: {}", bodyBytes.length, jsonMessage);
            } else {
                log.error("서버 연결이 없습니다.");
            }
        } catch (Exception e) {
            log.error("메시지 전송 실패", e);
        }
    }
}