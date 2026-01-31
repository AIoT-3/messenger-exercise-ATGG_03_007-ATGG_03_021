package com.message.config;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    
    // --- 서버 기본 설정 ---
    public static final String HOST = "localhost";
    public static final int PORT = 8080;
    public static final String PROTOCOL = "http";
    
    // --- API 경로 (Context Path) ---
    public static final String API_LOGIN_PATH = "/api/login";
    
    // --- 타임아웃 및 시스템 설정 ---
    public static final int BACKLOG = 0; // 서버 대기 큐 크기 (0은 시스템 기본값)
    public static final int READ_TIMEOUT_SEC = 5;
    
    // --- 유틸리티 메서드: 전체 URL 반환 ---
    public static String getBaseUrl() {
        return String.format("%s://%s:%d", PROTOCOL, HOST, PORT);
    }

    public static String getLoginUrl() {
        return getBaseUrl() + API_LOGIN_PATH;
    }

    //세션이 유효한지 체크 (비 로그인상태인지 확인)
    public static List<String> SessionCheckMethodName = new ArrayList<>();

    static {
        SessionCheckMethodName.add("LOGIN");
    }

}