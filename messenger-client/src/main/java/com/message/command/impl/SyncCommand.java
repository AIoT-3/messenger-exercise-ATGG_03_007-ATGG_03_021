package com.message.command.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.message.TypeManagement;
import com.message.command.ReceiveCommand;
import com.message.domain.MessageContent;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.RoomDto;
import com.message.dto.data.impl.SynchronizedDto;
import com.message.dto.data.impl.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 동기화 관련 Command 클래스들
 * 서버에서 푸시되는 동기화 메시지 처리 (유저 목록, 채팅방 목록)
 */
public class SyncCommand {

    /**
     * 유저 목록 동기화 Command
     * 다른 유저 로그인/로그아웃 시 서버에서 푸시
     */
    public static class UserSyncReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(UserSyncReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Sync.USER;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                // data가 List<UserInfo> 형태로 직접 전달됨 (@JsonValue 사용)
                List<UserDto.UserInfo> users = mapper.convertValue(
                    responseMessage.data(),
                    new TypeReference<List<UserDto.UserInfo>>() {}
                );

                SynchronizedDto.UserSync data = new SynchronizedDto.UserSync(users);

                log.debug("유저 목록 동기화 수신 - {} 명의 유저", users != null ? users.size() : 0);

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("유저 목록 동기화 파싱 실패", e);
                throw new RuntimeException("유저 목록 동기화 처리 실패", e);
            } catch (IllegalArgumentException e) {
                log.error("유저 목록 동기화 변환 실패", e);
                throw new RuntimeException("유저 목록 동기화 처리 실패", e);
            }
        }
    }

    /**
     * 채팅방 목록 동기화 Command
     * 채팅방 생성/삭제 시 서버에서 푸시
     */
    public static class RoomSyncReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(RoomSyncReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Sync.ROOM;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                // RoomDto.ListResponse 형태로 전달됨
                RoomDto.ListResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    RoomDto.ListResponse.class
                );

                log.debug("채팅방 목록 동기화 수신 - {} 개의 방", data.rooms() != null ? data.rooms().size() : 0);

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("채팅방 목록 동기화 파싱 실패", e);
                throw new RuntimeException("채팅방 목록 동기화 처리 실패", e);
            }
        }
    }

    /**
     * 채팅방 메시지 동기화 Command
     * 다른 유저가 채팅방에 메시지를 보낼 때 서버에서 푸시
     */
    public static class RoomChatSyncReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(RoomChatSyncReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Sync.ROOM_CHAT;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                SynchronizedDto.HistorySyncResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    SynchronizedDto.HistorySyncResponse.class
                );

                log.debug("채팅방 메시지 동기화 수신 - roomId: {}, messages: {}",
                    data.roomId(),
                    data.messages() != null ? data.messages().size() : 0);

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("채팅방 메시지 동기화 파싱 실패", e);
                throw new RuntimeException("채팅방 메시지 동기화 처리 실패", e);
            }
        }
    }

    /**
     * 귓속말 동기화 Command
     * 다른 유저가 귓속말을 보낼 때 서버에서 푸시
     */
    public static class PrivateChatSyncReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(PrivateChatSyncReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Sync.PRIVATE_CHAT;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                SynchronizedDto.PrivateSyncResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    SynchronizedDto.PrivateSyncResponse.class
                );

                log.debug("귓속말 동기화 수신 - from: {}, to: {}", data.senderId(), data.receiverId());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("귓속말 동기화 파싱 실패", e);
                throw new RuntimeException("귓속말 동기화 처리 실패", e);
            }
        }
    }
}