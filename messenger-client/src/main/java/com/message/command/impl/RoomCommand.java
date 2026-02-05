package com.message.command.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.TypeManagement;
import com.message.command.ReceiveCommand;
import com.message.command.SendCommand;
import com.message.domain.MessageContent;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.RoomDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 채팅방 관련 Command 클래스들
 * 채팅방 목록 조회 요청/응답 처리
 */
public class RoomCommand {

    /**
     * 채팅방 목록 요청 Command
     * 채팅방 목록 요청을 JSON 형식으로 변환하여 서버로 전송
     */
    public static class RoomListSendCommand extends SendCommand {
        private static final Logger log = LoggerFactory.getLogger(RoomListSendCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Room.LIST;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            HeaderDto.RequestHeader header = createRequestHeader(TypeManagement.Room.LIST);

            try {
                // 채팅방 목록 요청은 data가 필요 없음
                String json = mapper.writeValueAsString(new RequestDto(header, null));
                log.debug("채팅방 목록 요청 JSON 생성: {}", json);
                return json;
            } catch (JsonProcessingException e) {
                log.error("채팅방 목록 요청 JSON 변환 실패", e);
                throw new RuntimeException("채팅방 목록 요청 생성 실패", e);
            }
        }
    }

    /**
     * 채팅방 목록 응답 Command
     * 서버로부터 받은 채팅방 목록 응답을 DTO로 변환
     */
    public static class RoomListReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(RoomListReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Room.LIST_SUCCESS;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                // Header 파싱
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                // Data 파싱
                RoomDto.ListResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    RoomDto.ListResponse.class
                );

                log.debug("채팅방 목록 수신 - 방 개수: {}", data.rooms() != null ? data.rooms().size() : 0);

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("채팅방 목록 응답 파싱 실패", e);
                throw new RuntimeException("채팅방 목록 응답 처리 실패", e);
            }
        }
    }

    /**
     * 채팅방 생성 요청 Command
     * 채팅방 생성 요청을 JSON 형식으로 변환하여 서버로 전송
     */
    public static class RoomCreateSendCommand extends SendCommand {
        private static final Logger log = LoggerFactory.getLogger(RoomCreateSendCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Room.CREATE;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.RequestMessage requestMessage = (MessageContent.RequestMessage) message;
            String roomName = requestMessage.content();

            if (roomName == null || roomName.trim().isEmpty()) {
                throw new IllegalArgumentException("채팅방 이름이 비어있습니다.");
            }

            HeaderDto.RequestHeader header = createRequestHeader(TypeManagement.Room.CREATE);
            RoomDto.CreateRequest data = new RoomDto.CreateRequest(roomName.trim());

            try {
                String json = mapper.writeValueAsString(new RequestDto(header, data));
                log.debug("채팅방 생성 요청 JSON 생성: {}", json);
                return json;
            } catch (JsonProcessingException e) {
                log.error("채팅방 생성 요청 JSON 변환 실패", e);
                throw new RuntimeException("채팅방 생성 요청 생성 실패", e);
            }
        }
    }

    /**
     * 채팅방 생성 응답 Command
     * 서버로부터 받은 채팅방 생성 응답을 DTO로 변환
     */
    public static class RoomCreateReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(RoomCreateReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Room.CREATE_SUCCESS;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                // Header 파싱
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                // Data 파싱
                RoomDto.CreateResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    RoomDto.CreateResponse.class
                );

                log.debug("채팅방 생성 성공 - roomId: {}, roomName: {}", data.roomId(), data.roomName());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("채팅방 생성 응답 파싱 실패", e);
                throw new RuntimeException("채팅방 생성 응답 처리 실패", e);
            }
        }
    }

    /**
     * 채팅방 입장 요청 Command
     */
    public static class RoomEnterSendCommand extends SendCommand {
        private static final Logger log = LoggerFactory.getLogger(RoomEnterSendCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Room.ENTER;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.RequestMessage requestMessage = (MessageContent.RequestMessage) message;
            String content = requestMessage.content();

            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("채팅방 ID가 비어있습니다.");
            }

            long roomId;
            try {
                roomId = Long.parseLong(content.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("올바른 채팅방 ID가 아닙니다: " + content);
            }

            HeaderDto.RequestHeader header = createRequestHeader(TypeManagement.Room.ENTER);
            RoomDto.EnterRequest data = new RoomDto.EnterRequest(roomId);

            try {
                String json = mapper.writeValueAsString(new RequestDto(header, data));
                log.debug("채팅방 입장 요청 JSON 생성: {}", json);
                return json;
            } catch (JsonProcessingException e) {
                log.error("채팅방 입장 요청 JSON 변환 실패", e);
                throw new RuntimeException("채팅방 입장 요청 생성 실패", e);
            }
        }
    }

    /**
     * 채팅방 입장 응답 Command
     */
    public static class RoomEnterReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(RoomEnterReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Room.ENTER_SUCCESS;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                RoomDto.EnterResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    RoomDto.EnterResponse.class
                );

                log.debug("채팅방 입장 성공 - roomId: {}, users: {}", data.roomId(), data.users());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("채팅방 입장 응답 파싱 실패", e);
                throw new RuntimeException("채팅방 입장 응답 처리 실패", e);
            }
        }
    }

    /**
     * 채팅방 퇴장 요청 Command
     */
    public static class RoomExitSendCommand extends SendCommand {
        private static final Logger log = LoggerFactory.getLogger(RoomExitSendCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Room.EXIT;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.RequestMessage requestMessage = (MessageContent.RequestMessage) message;
            String content = requestMessage.content();

            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("채팅방 ID가 비어있습니다.");
            }

            long roomId;
            try {
                roomId = Long.parseLong(content.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("올바른 채팅방 ID가 아닙니다: " + content);
            }

            HeaderDto.RequestHeader header = createRequestHeader(TypeManagement.Room.EXIT);
            RoomDto.ExitRequest data = new RoomDto.ExitRequest(roomId);

            try {
                String json = mapper.writeValueAsString(new RequestDto(header, data));
                log.debug("채팅방 퇴장 요청 JSON 생성: {}", json);
                return json;
            } catch (JsonProcessingException e) {
                log.error("채팅방 퇴장 요청 JSON 변환 실패", e);
                throw new RuntimeException("채팅방 퇴장 요청 생성 실패", e);
            }
        }
    }

    /**
     * 채팅방 퇴장 응답 Command
     */
    public static class RoomExitReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(RoomExitReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Room.EXIT_SUCCESS;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                RoomDto.ExitResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    RoomDto.ExitResponse.class
                );

                log.debug("채팅방 퇴장 성공 - roomId: {}, message: {}", data.roomId(), data.message());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("채팅방 퇴장 응답 파싱 실패", e);
                throw new RuntimeException("채팅방 퇴장 응답 처리 실패", e);
            }
        }
    }
}
