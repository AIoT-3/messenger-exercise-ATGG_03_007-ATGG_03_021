package com.message.service;

import com.message.domain.AtomicLongIdManagement;
import com.message.domain.RoomManagement;
import com.message.domain.SessionManagement;
import com.message.dto.data.impl.RoomDto;
import com.message.exception.custom.BusinessException;
import com.message.service.room.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class RoomServiceImplTest {

    private RoomServiceImpl roomService;
    private RoomManagement roomManagement;

    @BeforeEach
    void setUp() throws Exception {
        roomManagement = RoomManagement.getInstance();
        Field roomsField = RoomManagement.class.getDeclaredField("rooms");
        roomsField.setAccessible(true);
        ((Map<?, ?>) roomsField.get(roomManagement)).clear();

        roomService = new RoomServiceImpl();
    }

    @Test
    @DisplayName("채팅방 생성 성공")
    void createRoom_success() {
        RoomDto.CreateRequest request = new RoomDto.CreateRequest("테스트방");

        RoomDto.CreateResponse response = roomService.createRoom(request);

        assertNotNull(response);
        assertEquals("테스트방", response.roomName());
        assertTrue(response.roomId() > 0);
    }

    @Test
    @DisplayName("중복 이름의 채팅방 생성 시 예외 발생")
    void createRoom_duplicateName_throwsException() {
        roomService.createRoom(new RoomDto.CreateRequest("중복방"));

        assertThrows(BusinessException.class, () -> roomService.createRoom(new RoomDto.CreateRequest("중복방")));
    }

    @Test
    @DisplayName("채팅방 목록 조회 - 빈 목록")
    void getRoomList_empty() {
        RoomDto.ListResponse response = roomService.getRoomList();

        assertNotNull(response);
        assertNotNull(response.rooms());
        assertTrue(response.rooms().isEmpty());
    }

    @Test
    @DisplayName("채팅방 목록 조회 - 생성 후 조회")
    void getRoomList_afterCreate() {
        roomService.createRoom(new RoomDto.CreateRequest("방A"));
        roomService.createRoom(new RoomDto.CreateRequest("방B"));

        RoomDto.ListResponse response = roomService.getRoomList();

        assertEquals(2, response.rooms().size());
    }

    @Test
    @DisplayName("존재하지 않는 방에 입장 시 예외 발생")
    void enterRoom_notFound_throwsException() {
        assertThrows(BusinessException.class,
            () -> roomService.enterRoom("sessionId", new RoomDto.EnterRequest(9999L)));
    }

    @Test
    @DisplayName("존재하지 않는 방에서 퇴장 시 예외 발생")
    void exitRoom_notFound_throwsException() {
        assertThrows(BusinessException.class,
            () -> roomService.exitRoom("sessionId", new RoomDto.ExitRequest(9999L)));
    }
}
