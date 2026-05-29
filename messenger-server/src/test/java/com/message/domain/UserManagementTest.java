package com.message.domain;

import com.message.entity.UserEntity;
import com.message.exception.custom.user.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserManagement 단위 테스트
 * 기본 등록 유저: admin, marco, jaemin
 */
class UserManagementTest {

    private final UserManagement userManagement = UserManagement.getInstance();

    @Test
    @DisplayName("싱글톤 인스턴스 동일성 확인")
    void singleton_sameInstance() {
        assertSame(UserManagement.getInstance(), UserManagement.getInstance());
    }

    @Test
    @DisplayName("기본 등록된 유저 조회 성공")
    void getUser_defaultUser_success() {
        UserEntity user = userManagement.getUser("admin");

        assertNotNull(user);
        assertEquals("admin", user.getUserId());
        assertEquals("1234", user.getPassWord());
    }

    @Test
    @DisplayName("존재하지 않는 유저 조회 시 예외 발생")
    void getUser_notFound_throwsException() {
        assertThrows(UserNotFoundException.class, () -> userManagement.getUser("no_such_user"));
    }

    @Test
    @DisplayName("초기 유저 데이터(marco) 비밀번호 확인")
    void getUser_marco_correctPassword() {
        UserEntity user = userManagement.getUser("marco");

        assertEquals("nhnacademy123", user.getPassWord());
        assertEquals("마르코", user.getName());
    }
}
