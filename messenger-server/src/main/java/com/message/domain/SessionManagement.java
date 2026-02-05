package com.message.domain;

import com.message.exception.custom.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    // 귓속말 할 때 필요함
    public static String getSessionId(String userId) {
        if (Objects.isNull(userId) || userId.isBlank()) {
            throw new BusinessException(ErrorManagement.User.INVALID_INPUT, "유효하지 않은 유저 아이디입니다.", 400);
        }

        // 맵 돌면서 파라미터로 받은 유저아이디 가진 첫 번째 세션 아이디 찾기
        return sessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(userId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static List<String> getSessionIdList(List<String> userIdList) {
        if (Objects.isNull(userIdList) || userIdList.isEmpty()) {
            throw new BusinessException(ErrorManagement.User.INVALID_INPUT, "사용자 리스트가 비었습니다.", 400);
        }

        Set<String> userIdSet = new HashSet<>(userIdList);
        return sessions.entrySet().stream()
                .filter(s -> userIdSet.contains(s.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    public static void deleteSession(String uuid) {
        // 1. 일단 지워본다. 지워진 값이 있다면 그 값을 반환하고, 없으면 null을 반환한다.
        String removedUserId = sessions.remove(uuid);

        // 2. null이라면 존재하지 않았다는 뜻이므로 예외 처리
        if (removedUserId == null) {
            log.warn("[세션 삭제 실패] 존재하지 않는 UUID: {}", uuid);
            throw new BusinessException(ErrorManagement.Session.NOT_FOUND, "존재하지 않는 세션입니다.", 404);
        }

        log.debug("[세션 삭제 성공] UserID: {}, UUID: {}", removedUserId, uuid);
    }

    public static List<String> getAllUsers() {
        return new ArrayList<>(sessions.values());
    }

    public static List<String> getAllSessionIds() {
        return new ArrayList<>(sessions.keySet());
    }
}