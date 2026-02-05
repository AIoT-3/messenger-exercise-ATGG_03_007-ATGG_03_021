package com.message.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.message.TypeManagement;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.dto.data.impl.AuthDto;
import com.message.exception.custom.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// TODO 구현사항 (재민)

@Slf4j
public class SocketManagement {
    // 세션 아이디 -> 실제 소켓
    private static final Map<String, Socket> socketMap = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static void addSocket(String sessionId, Socket socket) {
        if (Objects.isNull(sessionId) || Objects.isNull(socket)) {
            return;
        }
        socketMap.put(sessionId, socket);
        log.debug("[SocketManagement] 소켓 등록 - sessionId: {}", sessionId);
    }

    public static void removeSocket(String sessionId) {
        if (Objects.nonNull(sessionId)) {
            socketMap.remove(sessionId);
            log.debug("[SocketManagement] 소켓 제거: sessionId: {}", sessionId);
        }
    }

    // TODO 동건이형이 추가함
    public static void checkSocket(String type, Object o, Socket socket) {
        try {
            switch (type) {
                case TypeManagement.Auth.LOGIN -> {
                    if (o instanceof AuthDto.LoginResponse response) {
                        addSocket(response.sessionId(), socket);
                    }
                }
                case TypeManagement.Auth.LOGOUT -> {
                    if (o instanceof HeaderDto.RequestHeader header) {
                        removeSocket(header.sessionId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("[SocketManagement] 소켓 상태 체크 중 오류 발생", e);
        }
    }

    // TODO 동건이형이 추가함
    public static Socket getSocket(String sessionId) {
        if (Objects.isNull(sessionId) || sessionId.isBlank()) {
            throw new BusinessException(ErrorManagement.Session.NOT_FOUND, "존재하지 않는 세션입니다.", 404);
        }
        return socketMap.get(sessionId);
    }

    // TODO 동건이형이 추가함
    public static List<Socket> getSocketList(List<String> sessionIdList) {
        if (Objects.isNull(sessionIdList) || sessionIdList.isEmpty()) {
            // return Collections.emptyList(); // TODO 이렇게 빈 리스트 반환하는게 낫지 않나?
            throw new BusinessException(ErrorManagement.Session.NOT_FOUND, "존재하지 않는 세션아이디 리스트입니다.", 404);
        }

        Set<String> sessionIdSet = new HashSet<>(sessionIdList);
        return socketMap.entrySet().stream()
                .filter(s -> sessionIdSet.contains(s.getKey()))
                .map(Map.Entry::getValue)
                .toList();

        // TODO 그냥 이렇게 써도 되지 않나?
//        return sessionIdList.stream()
//                .map(socketMap::get) // 세션아이디로 소켓 찾기
//                .filter(Objects::nonNull) // 맵에 없는 경우 제외
//                .filter(s -> !s.isClosed()) // 닫힌 소켓 제외
//                .toList();
    }

    // TODO send를 하긴 해야 하지만, 이건 매니지먼트인데 여기 있는게 맞아? 고민하셈. 일단 여기다.
    public static void sendMessage(String sessionId, Object data) {
        Socket socket = socketMap.get(sessionId);

        if (Objects.isNull(socket) || socket.isClosed()) {
            log.warn("[SocketManagement] 전송 실패 - 소켓이 없거나 닫힘: {}", sessionId);
            return;
        }

        try {
            // 여기서 제이슨 직접 변환. 서비스 쪽 일 덜어주기
            // data가 이미 String이면 그대로 쓰고, 객체면 제이슨으로 변환
            String json = (data instanceof String) ? (String) data : objectMapper.writeValueAsString(data);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(json);

            log.debug("[SocketManagement] 메시지 전송 완료 - To: {}", sessionId);
        } catch (Exception e) {
            log.error("[SocketManagement] 전송 중 오류 - sessionId: {}", sessionId, e);
        }
    }
}
