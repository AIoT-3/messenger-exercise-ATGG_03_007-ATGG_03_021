package com.message.domain;

import com.message.exception.custom.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SessionManagement 단위 테스트
 * 테스트 격리를 위해 리플렉션으로 내부 sessions 맵을 초기화
 */
class SessionManagementTest {

    private SessionManagement sessionManagement;

    @BeforeEach
    void setUp() throws Exception {
        sessionManagement = SessionManagement.getInstance();
        // 테스트 간 격리를 위해 내부 세션 맵 초기화
        Field sessionsField = SessionManagement.class.getDeclaredField("sessions");
        sessionsField.setAccessible(true);
        ((Map<?, ?>) sessionsField.get(sessionManagement)).clear();
    }

    @Test
    @DisplayName("세션 추가 후 조회 성공")
    void addAndGetSession() {
        sessionManagement.addSessions("uuid-001", "alice");

        assertEquals("alice", sessionManagement.getUserId("uuid-001"));
        assertTrue(sessionManagement.isExistedUuid("uuid-001"));
        assertTrue(sessionManagement.isExistedUserId("alice"));
    }

    @Test
    @DisplayName("존재하지 않는 UUID 조회 시 null 반환")
    void getSession_notFound_returnsNull() {
        assertNull(sessionManagement.getUserId("non-existent-uuid"));
        assertFalse(sessionManagement.isExistedUuid("non-existent-uuid"));
    }

    @Test
    @DisplayName("세션 삭제 성공")
    void deleteSession_success() {
        sessionManagement.addSessions("uuid-002", "bob");
        sessionManagement.deleteSession("uuid-002");

        assertFalse(sessionManagement.isExistedUuid("uuid-002"));
    }

    @Test
    @DisplayName("존재하지 않는 세션 삭제 시 예외 발생")
    void deleteSession_notFound_throwsException() {
        assertThrows(BusinessException.class, () -> sessionManagement.deleteSession("no-such-uuid"));
    }

    @Test
    @DisplayName("NULL uuid로 세션 추가 시 예외 발생")
    void addSessions_nullUuid_throwsException() {
        assertThrows(BusinessException.class, () -> sessionManagement.addSessions(null, "user"));
    }

    @Test
    @DisplayName("NULL userId로 세션 추가 시 예외 발생")
    void addSessions_nullUserId_throwsException() {
        assertThrows(BusinessException.class, () -> sessionManagement.addSessions("uuid", null));
    }

    @Test
    @DisplayName("전체 세션 목록 조회")
    void getAllUsers_returnsAllConnectedUsers() {
        sessionManagement.addSessions("s1", "u1");
        sessionManagement.addSessions("s2", "u2");

        List<String> users = sessionManagement.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.contains("u1"));
        assertTrue(users.contains("u2"));
    }

    @Test
    @DisplayName("userId로 sessionId 역조회")
    void getSessionId_byUserId() {
        sessionManagement.addSessions("session-xyz", "charlie");

        assertEquals("session-xyz", sessionManagement.getSessionId("charlie"));
    }
}
