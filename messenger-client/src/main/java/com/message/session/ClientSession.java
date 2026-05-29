package com.message.session;

/**
 * 현재 로그인한 사용자의 세션 상태(세션 ID, 유저 ID, 현재 방 ID)만 관리.
 * 소켓 연결 및 I/O는 {@link com.message.connection.ClientConnection}이 담당한다.
 */
public class ClientSession {

    private static String sessionId;
    private static String userId;
    private static long currentRoomId = 1;

    private ClientSession() {}

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String sessionId) {
        ClientSession.sessionId = sessionId;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        ClientSession.userId = userId;
    }

    public static long getCurrentRoomId() {
        return currentRoomId;
    }

    public static void setCurrentRoomId(long currentRoomId) {
        ClientSession.currentRoomId = currentRoomId;
    }

    public static boolean isAuthenticated() {
        return sessionId != null;
    }

    public static void clear() {
        sessionId = null;
        userId = null;
        currentRoomId = 1;
    }
}
