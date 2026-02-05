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

    @Override
    public RoomDto.CreateResponse createRoom(RoomDto.CreateRequest request) {
        boolean isExist = RoomManagement.getAllRooms().stream()
                .anyMatch(room -> room.getRoomName().equals(request.roomName()));

        if(isExist) {
            throw new BusinessException(ErrorManagement.Room.ALREADY_EXISTS, "이미 존재하는 채팅방입니다.", 400);
        }

        // 아이디 생성
        long roomId = AtomicLongIdManagement.getRoomIdSequenceIncreateAndGet();

        // 엔티티 생성
        RoomEntity newRoom = new RoomEntity(roomId, request.roomName(), 0);

        // 엔티티 저장
        RoomManagement.addRoom(newRoom);

        // 리스폰스 디티오 변환
        return new RoomDto.CreateResponse(roomId, request.roomName());
    }

    @Override
    public RoomDto.ListResponse getRoomList() {
        // 모든 방 가져오기, 변환
        List<RoomDto.RoomSummary> summaries = RoomManagement.getAllRooms().stream()
                .map(room -> new RoomDto.RoomSummary(
                        room.getRoomId(),
                        room.getRoomName(),
                        room.getUserCount()))
                .toList();

        return new RoomDto.ListResponse(summaries);
    }

    @Override
    public RoomDto.EnterResponse enterRoom(String sessionId, RoomDto.EnterRequest request) {
        // 방 찾자
        RoomEntity room = RoomManagement.getRoom(request.roomId());

        if (Objects.isNull(room)) {
            throw new BusinessException(ErrorManagement.Room.NOT_FOUND, "채팅방을 찾을 수 없습니다.", 404);
        }

        // 방에 입장 (세션 아이디 추가)
        room.addParticipant(sessionId);

        List<String> userList = room.getParticipantSessionIds().stream()
                .map(SessionManagement::getUserId) // 여기서부터
                .filter(Objects::nonNull) // 널체크함
                .toList();

        log.debug("[채팅방 입장 완료] RoomId: {}, UserId: {}", room.getRoomId(), userList);

        return new RoomDto.EnterResponse(room.getRoomId(), userList);
    }

    @Override
    public void exitRoom(String sessionId, RoomDto.ExitRequest request) {
        // 방 찾기
        RoomEntity room = RoomManagement.getRoom(request.roomId());

        if (Objects.isNull(room)) {
            throw new BusinessException(ErrorManagement.Room.NOT_FOUND, "채팅방을 찾을 수 없습니다.", 404);
        }

        // 방이 존재한다면 나가라
        room.removeParticipant(sessionId);
        log.debug("[채팅방 나가기 완료] RoomId: {}, SessionId: {}", request.roomId(), sessionId);
    }
}
