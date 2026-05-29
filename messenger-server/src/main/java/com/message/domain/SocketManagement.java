package com.message.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.message.TypeManagement;
import com.message.cofig.AppConfig;
import com.message.dto.HeaderDto;
import com.message.dto.data.impl.AuthDto;
import com.message.exception.custom.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketManagement {

    private static final SocketManagement INSTANCE = new SocketManagement();

    private final Map<String, Socket> socketMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private SocketManagement() {}

    public static SocketManagement getInstance() {
        return INSTANCE;
    }

    public void addSocket(String sessionId, Socket socket) {
        if (Objects.isNull(sessionId) || Objects.isNull(socket)) {
            return;
        }
        socketMap.put(sessionId, socket);
        log.debug("[SocketManagement] 소켓 등록 - sessionId: {}", sessionId);
    }

    public void removeSocket(String sessionId) {
        if (Objects.nonNull(sessionId)) {
            socketMap.remove(sessionId);
            log.debug("[SocketManagement] 소켓 제거: sessionId: {}", sessionId);
        }
    }

    public void removeSocket(Socket socket) {
        if (Objects.nonNull(socket) && socketMap.containsValue(socket)) {
            socketMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(socket))
                    .findFirst()
                    .ifPresent(entry -> removeSocket(entry.getKey()));
        }
    }

    public void checkSocket(String type, Object o, Socket socket) {
        try {
            switch (type) {
                case TypeManagement.Auth.LOGIN -> {
                    if (o instanceof AuthDto.LoginResponse response) {
                        addSocket(response.sessionId(), socket);
                    } else {
                        throw new BusinessException(ErrorManagement.Request.IS_NULL, "LOGIN 응답 객체가 올바르지 않습니다.", 400);
                    }
                }
                case TypeManagement.Auth.LOGOUT -> {
                    if (o instanceof HeaderDto.RequestHeader header) {
                        removeSocket(header.sessionId());
                    } else {
                        throw new BusinessException(ErrorManagement.Request.IS_NULL, "LOGOUT 헤더 객체가 올바르지 않습니다.", 400);
                    }
                }
            }
        } catch (Exception e) {
            log.error("[SocketManagement] 소켓 상태 체크 중 오류 발생", e);
        }
    }

    public Socket getSocket(String sessionId) {
        if (Objects.isNull(sessionId) || sessionId.isBlank()) {
            throw new BusinessException(ErrorManagement.Session.NOT_FOUND, "존재하지 않는 세션입니다.", 404);
        }
        return socketMap.get(sessionId);
    }

    public List<Socket> getSocketList(List<String> sessionIdList) {
        if (Objects.isNull(sessionIdList) || sessionIdList.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> sessionIdSet = new HashSet<>(sessionIdList);
        return socketMap.entrySet().stream()
                .filter(s -> sessionIdSet.contains(s.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }

    public void sendMessage(String sessionId, Object data) {
        Socket socket = getSocket(sessionId);

        if (Objects.isNull(socket) || socket.isClosed()) {
            log.warn("[SocketManagement] 전송 실패 - 소켓이 없거나 닫힘: {}", sessionId);
            return;
        }

        try {
            String json = (data instanceof String) ? (String) data : objectMapper.writeValueAsString(data);

            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println("%s%s%n".formatted(AppConfig.MESSAGE_LENGTH, json.getBytes(StandardCharsets.UTF_8).length));
            out.println(json);
            out.flush();

            log.debug("[SocketManagement] 메시지 전송 완료 - To: {}", sessionId);
        } catch (Exception e) {
            log.error("[SocketManagement] 전송 중 오류 - sessionId: {}", sessionId, e);
        }
    }

    public void sendSynchronizedMessage(List<String> sessionIds, String message) {
        log.debug("[동기화 메시지] 송신 메시지 : {}", message);
        List<Socket> socketList = getSocketList(sessionIds);
        log.debug("[동기화 메시지] 동기화 할 유저수 : {}", socketList.size());
        byte[] header = "%s%s%n".formatted(AppConfig.MESSAGE_LENGTH, message.getBytes(StandardCharsets.UTF_8).length).getBytes(StandardCharsets.UTF_8);
        byte[] body = message.getBytes(StandardCharsets.UTF_8);
        socketList.forEach(s -> {
            log.debug("[소캣 통신] 소캣 종류:{}", Objects.isNull(s.getChannel()) ? "일반 소캣" : "채널 소캣");
            try {
                if (Objects.nonNull(s.getChannel())) {
                    SocketChannel sc = s.getChannel();
                    ByteBuffer combined = ByteBuffer.allocate(header.length + body.length);
                    combined.put(header);
                    combined.put(body);
                    combined.flip();
                    while (combined.hasRemaining()) {
                        sc.write(combined);
                    }
                } else {
                    OutputStream out = s.getOutputStream();
                    out.write(header);
                    out.write(body);
                    out.flush();
                }
            } catch (IOException e) {
                log.error("[동기화 메시지] 메시지 전송 실패 - message:{}", e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
}
