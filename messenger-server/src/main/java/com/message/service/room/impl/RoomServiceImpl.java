package com.message.service.room.impl;

import com.message.domain.AtomicLongIdManagement;
import com.message.domain.ErrorManagement;
import com.message.domain.RoomManagement;
import com.message.domain.SessionManagement;
import com.message.dto.data.impl.RoomDto;
import com.message.entity.RoomEntity;
import com.message.exception.custom.BusinessException;
import com.message.service.room.RoomService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class RoomServiceImpl implements RoomService {

    private final AtomicLongIdManagement idManagement;
    private final RoomManagement roomManagement;
    private final SessionManagement sessionManagement;

    public RoomServiceImpl() {
        this(AtomicLongIdManagement.getInstance(), RoomManagement.getInstance(), SessionManagement.getInstance());
    }

    RoomServiceImpl(AtomicLongIdManagement idManagement, RoomManagement roomManagement, SessionManagement sessionManagement) {
        this.idManagement = idManagement;
        this.roomManagement = roomManagement;
        this.sessionManagement = sessionManagement;
    }

    @Override
    public RoomDto.CreateResponse createRoom(RoomDto.CreateRequest request) {
        boolean isExist = roomManagement.getAllRooms().stream()
                .anyMatch(room -> room.getRoomName().equals(request.roomName()));

        if (isExist) {
            throw new BusinessException(ErrorManagement.Room.ALREADY_EXISTS, "이미 존재하는 채팅방입니다.", 400);
        }

        long roomId = idManagement.getRoomIdSequenceIncrementAndGet();
        RoomEntity newRoom = new RoomEntity(roomId, request.roomName(), 0);
        roomManagement.addRoom(newRoom);

        return new RoomDto.CreateResponse(roomId, request.roomName());
    }

    @Override
    public RoomDto.ListResponse getRoomList() {
        List<RoomDto.RoomSummary> summaries = roomManagement.getAllRooms().stream()
                .map(room -> new RoomDto.RoomSummary(room.getRoomId(), room.getRoomName(), room.getUserCount()))
                .toList();
        return new RoomDto.ListResponse(summaries);
    }

    @Override
    public RoomDto.EnterResponse enterRoom(String sessionId, RoomDto.EnterRequest request) {
        RoomEntity room = roomManagement.getRoom(request.roomId());
        if (Objects.isNull(room)) {
            throw new BusinessException(ErrorManagement.Room.NOT_FOUND, "채팅방을 찾을 수 없습니다.", 404);
        }

        String userId = sessionManagement.getUserId(sessionId);
        room.addParticipant(userId);

        List<String> userList = room.getParticipantUserIds().stream()
                .map(sessionManagement::getUserId)
                .filter(Objects::nonNull)
                .toList();

        log.debug("[채팅방 입장 완료] RoomId: {}, UserId: {}", room.getRoomId(), userList);
        return new RoomDto.EnterResponse(room.getRoomId(), userList);
    }

    @Override
    public void exitRoom(String sessionId, RoomDto.ExitRequest request) {
        RoomEntity room = roomManagement.getRoom(request.roomId());
        if (Objects.isNull(room)) {
            throw new BusinessException(ErrorManagement.Room.NOT_FOUND, "채팅방을 찾을 수 없습니다.", 404);
        }

        String userId = sessionManagement.getUserId(sessionId);
        room.removeParticipant(userId);
        log.debug("[채팅방 나가기 완료] RoomId: {}, SessionId: {}", request.roomId(), sessionId);
    }
}
