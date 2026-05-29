package com.message.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SessionContextTest {

    @AfterEach
    void tearDown() {
        SessionContext.clear();
    }

    @Test
    @DisplayName("set 후 get 으로 동일 값 반환")
    void setAndGet() {
        SessionContext.set("session-abc");
        assertEquals("session-abc", SessionContext.get());
    }

    @Test
    @DisplayName("clear 후 null 반환")
    void clear_returnsNull() {
        SessionContext.set("session-abc");
        SessionContext.clear();
        assertNull(SessionContext.get());
    }

    @Test
    @DisplayName("스레드 간 격리 확인 - 다른 스레드에서 설정해도 현재 스레드에 영향 없음")
    void threadIsolation() throws InterruptedException {
        SessionContext.set("main-thread-session");

        AtomicReference<String> otherThreadValue = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Thread other = new Thread(() -> {
            SessionContext.set("other-thread-session");
            otherThreadValue.set(SessionContext.get());
            latch.countDown();
        });
        other.start();
        latch.await();

        // 현재 스레드의 값은 변경되지 않아야 함
        assertEquals("main-thread-session", SessionContext.get());
        assertEquals("other-thread-session", otherThreadValue.get());
    }

    @Test
    @DisplayName("초기 상태에서 get 은 null 반환")
    void get_withoutSet_returnsNull() {
        assertNull(SessionContext.get());
    }
}
