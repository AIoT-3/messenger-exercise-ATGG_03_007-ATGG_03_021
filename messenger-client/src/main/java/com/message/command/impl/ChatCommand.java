package com.message.command.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.TypeManagement;
import com.message.command.ReceiveCommand;
import com.message.command.SendCommand;
import com.message.domain.MessageContent;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.ChatDto;
import com.message.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 채팅 관련 Command 클래스들
 * 채팅 메시지 송수신 처리
 */
public class ChatCommand {

    /**
     * 채팅 메시지 전송 Command
     * 사용자 입력을 JSON 형식으로 변환하여 서버로 전송
     */
    public static class MessageSendCommand extends SendCommand {
        private static final Logger log = LoggerFactory.getLogger(MessageSendCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Chat.MESSAGE;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.RequestMessage requestMessage = (MessageContent.RequestMessage) message;
            String content = requestMessage.content();

            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("메시지 내용이 비어있습니다.");
            }

            HeaderDto.RequestHeader header = createRequestHeader(requestMessage.type());
            ChatDto.MessageRequest data = new ChatDto.MessageRequest(
                ClientSession.getCurrentRoomId(),
                content.trim()
            );

            try {
                String json = mapper.writeValueAsString(new RequestDto(header, data));
                log.debug("채팅 메시지 요청 JSON 생성: {}", json);
                return json;
            } catch (JsonProcessingException e) {
                log.error("채팅 메시지 요청 JSON 변환 실패", e);
                throw new RuntimeException("채팅 메시지 요청 생성 실패", e);
            }
        }
    }

    /**
     * 채팅 메시지 성공 응답 Command
     * 서버로부터 받은 채팅 전송 성공 응답을 DTO로 변환
     */
    public static class MessageReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(MessageReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Chat.MESSAGE_SUCCESS;
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
                ChatDto.MessageResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    ChatDto.MessageResponse.class
                );

                log.debug("채팅 메시지 전송 성공 - roomId: {}, messageId: {}", data.roomId(), data.messageId());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("채팅 메시지 응답 파싱 실패", e);
                throw new RuntimeException("채팅 메시지 응답 처리 실패", e);
            }
        }
    }

    /**
     * 귓속말 전송 Command
     */
    public static class PrivateSendCommand extends SendCommand {
        private static final Logger log = LoggerFactory.getLogger(PrivateSendCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Chat.PRIVATE;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.RequestMessage requestMessage = (MessageContent.RequestMessage) message;
            String content = requestMessage.content();

            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("귓속말 정보가 올바르지 않습니다.");
            }

            // 형식: "receiverId message"
            String[] parts = content.trim().split("\\s+", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("받는 사람 ID와 메시지를 모두 입력해주세요.");
            }

            HeaderDto.RequestHeader header = createRequestHeader(requestMessage.type());
            ChatDto.PrivateRequest data = new ChatDto.PrivateRequest(
                ClientSession.getUserId(),
                parts[0].trim(),
                parts[1].trim()
            );

            try {
                String json = mapper.writeValueAsString(new RequestDto(header, data));
                log.debug("귓속말 요청 JSON 생성: {}", json);
                return json;
            } catch (JsonProcessingException e) {
                log.error("귓속말 요청 JSON 변환 실패", e);
                throw new RuntimeException("귓속말 요청 생성 실패", e);
            }
        }
    }

    /**
     * 귓속말 성공 응답 Command
     */
    public static class PrivateReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(PrivateReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Chat.PRIVATE_SUCCESS;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                ChatDto.PrivateResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    ChatDto.PrivateResponse.class
                );

                log.debug("귓속말 수신 - from: {}, to: {}", data.senderId(), data.receiverId());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("귓속말 응답 파싱 실패", e);
                throw new RuntimeException("귓속말 응답 처리 실패", e);
            }
        }
    }

    /**
     * 채팅 기록 조회 요청 Command
     */
    public static class HistorySendCommand extends SendCommand {
        private static final Logger log = LoggerFactory.getLogger(HistorySendCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Chat.HISTORY;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.RequestMessage requestMessage = (MessageContent.RequestMessage) message;
            String content = requestMessage.content();

            long roomId = ClientSession.getCurrentRoomId();
            int limit = 50; // 기본값
            long beforeMessageId = 0; // 0은 최신 메시지부터

            // 형식: "roomId limit beforeMessageId" 또는 "roomId limit" 또는 "roomId" 또는 빈 값
            if (content != null && !content.trim().isEmpty()) {
                String[] parts = content.trim().split("\\s+");
                try {
                    if (parts.length >= 1) {
                        roomId = Long.parseLong(parts[0]);
                    }
                    if (parts.length >= 2) {
                        limit = Integer.parseInt(parts[1]);
                    }
                    if (parts.length >= 3) {
                        beforeMessageId = Long.parseLong(parts[2]);
                    }
                } catch (NumberFormatException e) {
                    log.warn("채팅 기록 조회 파라미터 파싱 실패, 기본값 사용: {}", content);
                }
            }

            HeaderDto.RequestHeader header = createRequestHeader(TypeManagement.Chat.HISTORY);
            ChatDto.HistoryRequest data = new ChatDto.HistoryRequest(roomId, limit, beforeMessageId);

            try {
                String json = mapper.writeValueAsString(new RequestDto(header, data));
                log.debug("채팅 기록 조회 요청 JSON 생성: {}", json);
                return json;
            } catch (JsonProcessingException e) {
                log.error("채팅 기록 조회 요청 JSON 변환 실패", e);
                throw new RuntimeException("채팅 기록 조회 요청 생성 실패", e);
            }
        }
    }

    /**
     * 채팅 기록 조회 응답 Command
     */
    public static class HistoryReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(HistoryReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Chat.HISTORY_SUCCESS;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                ChatDto.HistoryResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    ChatDto.HistoryResponse.class
                );

                log.debug("채팅 기록 수신 - roomId: {}, messages: {}, hasMore: {}",
                    data.roomId(),
                    data.messages() != null ? data.messages().size() : 0,
                    data.hasMore());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("채팅 기록 응답 파싱 실패", e);
                throw new RuntimeException("채팅 기록 응답 처리 실패", e);
            }
        }
    }

    /**
     * 실시간 채팅 메시지 수신 Command (서버 푸시)
     */
    public static class ChatMessageReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(ChatMessageReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Chat.MESSAGE_RECEIVE;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                ChatDto.ChatMessage data = mapper.treeToValue(
                    responseMessage.data(),
                    ChatDto.ChatMessage.class
                );

                log.debug("실시간 채팅 메시지 수신 - senderId: {}, content: {}", data.senderId(), data.content());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("실시간 채팅 메시지 파싱 실패", e);
                throw new RuntimeException("실시간 채팅 메시지 처리 실패", e);
            }
        }
    }

    /**
     * 실시간 귓속말 수신 Command (서버 푸시)
     */
    public static class PrivateMessageReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(PrivateMessageReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.Chat.PRIVATE_MESSAGE_RECEIVE;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            MessageContent.ResponseMessage responseMessage = (MessageContent.ResponseMessage) message;

            try {
                HeaderDto.ResponseHeader header = mapper.treeToValue(
                    responseMessage.header(),
                    HeaderDto.ResponseHeader.class
                );

                ChatDto.PrivateResponse data = mapper.treeToValue(
                    responseMessage.data(),
                    ChatDto.PrivateResponse.class
                );

                log.debug("실시간 귓속말 수신 - from: {}, to: {}", data.senderId(), data.receiverId());

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("실시간 귓속말 파싱 실패", e);
                throw new RuntimeException("실시간 귓속말 처리 실패", e);
            }
        }
    }
}