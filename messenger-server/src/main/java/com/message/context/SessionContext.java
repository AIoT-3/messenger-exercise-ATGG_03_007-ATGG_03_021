package com.message.context;

/**
 * 요청 처리 스레드별 세션 ID를 ThreadLocal로 관리.
 * MessageDispatcher가 요청 시작 시 set, 종료 시 clear를 호출한다.
 */
public class SessionContext {

    private static final ThreadLocal<String> sessionIdHolder = new ThreadLocal<>();

    private SessionContext() {}

    public static void set(String sessionId) {
        sessionIdHolder.set(sessionId);
    }

    public static String get() {
        return sessionIdHolder.get();
    }

    public static void clear() {
        sessionIdHolder.remove();
    }
}
