package com.message.domain;

import com.message.exception.custom.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionManagement {
    private static final Map<String, String> sessions = new ConcurrentHashMap<>();

    public static void addSessions(String userId, String uuid) {
        //TODO 검증하셈
        if(Objects.isNull(userId) || Objects.isNull(uuid) || userId.isBlank() || uuid.isBlank()) {
            log.error("[세션 추가] 값이 존재하지 않습니다");
            throw new BusinessException(ErrorManagement.Server.SERVER_DOWN, "서버 다운", 500);
        }

        sessions.put(uuid, userId);
    }
}
