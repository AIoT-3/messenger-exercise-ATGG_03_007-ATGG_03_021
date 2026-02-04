package com.message.domain;

import com.message.exception.custom.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionManagement {
    private static final Map<String, String> sessions = new ConcurrentHashMap<>();

    public static void addSessions(String uuid, String userId) {
        if (Objects.isNull(userId) || Objects.isNull(uuid) || userId.isBlank() || uuid.isBlank()) {
            log.error("[세션 등록] 값이 존재하지 않습니다");
            throw new BusinessException(ErrorManagement.Server.SERVER_DOWN, "서버 다운", 500);
        }

        sessions.put(uuid, userId);
        log.debug("[세션 등록 완료] User: {}, SessionId: {}", userId, uuid);
    }

    public static boolean isExistedUuid(String uuid) {
        return sessions.containsKey(uuid);
    }

    public static boolean isExistedUserId(String userId) {
        return sessions.containsValue(userId);
    }

    public static String getUserId(String uuid) {
        return sessions.get(uuid);
    }

    public static void deleteSession(String uuid) {
        // 1. 일단 지워본다. 지워진 값이 있다면 그 값을 반환하고, 없으면 null을 반환한다.
        String removedUserId = sessions.remove(uuid);

        // 2. null이라면 존재하지 않았다는 뜻이므로 예외 처리
        if (removedUserId == null) {
            log.warn("[세션 삭제 실패] 존재하지 않는 UUID: {}", uuid);
            throw new IllegalArgumentException("존재하지 않는 uuid입니다.");
        }

        log.debug("[세션 삭제 성공] UserID: {}, UUID: {}", removedUserId, uuid);
    }

    public static List<String> getAllUsers() {
        return new ArrayList<>(sessions.values());
    }

}
