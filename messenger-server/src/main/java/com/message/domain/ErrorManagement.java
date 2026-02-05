package com.message.domain;

public class ErrorManagement {
    // --- 인증 및 권한 (Auth) ---
    public static class Auth {
        /** 아이디 또는 비밀번호가 일치하지 않음 */
        public static final String INVALID_CREDENTIALS = "AUTH.INVALID_CREDENTIALS";
        /** 세션이 만료되었거나 존재하지 않음 */
        public static final String INVALID_SESSION = "AUTH.INVALID_SESSION";
        /** 접근 권한이 없음 */
        public static final String UNAUTHORIZED = "AUTH.UNAUTHORIZED";
    }

    // --- 사용자 (User) ---
    public static class User {
        /** 해당 사용자를 찾을 수 없음 (귓속말 등) */
        public static final String NOT_FOUND = "USER.NOT_FOUND";
        /** 이미 존재하는 사용자 아이디로 생성 시도 */
        public static final String ALREADY_EXISTS = "USER.ALREADY_EXISTS";

        public static final String INVALID_INPUT = "USER.INVALID_INPUT";
    }

    // --- 채팅방 (Room) ---
    public static class Room {
        /** 해당 채팅방이 존재하지 않음 */
        public static final String NOT_FOUND = "ROOM.NOT_FOUND";
        /** 이미 존재하는 채팅방 이름으로 생성 시도 */
        public static final String ALREADY_EXISTS = "ROOM.ALREADY_EXISTS";
    }

    // --- 매퍼 (Mapper) ---
    public static class Mapper {
        /** ObjectMapper 가 객체 변환 실패 */
        public static final String FAIL_MAPPING = "MAPPER.FAIL_MAPPING";
    }

    // --- 핸들러 (Handler) ---
    public static class Handler {
        /** 해당하는 핸들러를 찾을 수 없음 */
        public static final String NOT_FOUND = "HANDLER.NOT_FOUND";
    }

    public static class Session {
        public static final String NOT_FOUND = "SESSION.NOT_FOUND";
        public static final String ALREADY_EXISTS = "SESSION.ALREADY_EXISTS";
    }

    public static class Server {
        public static final String SERVER_DOWN = "SERVER.SERVER_DOWN";
    }

    // --- 요청 관련 (헤더나 데이터 비었을 때) ---
    public static class Request {
        public static final String IS_NULL = "REQUEST.IS_NULL";
    }

}
