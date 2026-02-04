package com.message.command.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.message.TypeManagement;
import com.message.command.ReceiveCommand;
import com.message.command.SendCommand;
import com.message.domain.MessageContent;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 사용자 관련 Command 클래스들
 * 사용자 목록 조회 요청/응답 처리
 */
public class UserCommand {

    /**
     * 사용자 목록 요청 Command
     * 접속 중인 사용자 목록 요청을 JSON 형식으로 변환하여 서버로 전송
     */
    public static class UserListSendCommand extends SendCommand {
        private static final Logger log = LoggerFactory.getLogger(UserListSendCommand.class);

        @Override
        public String getType() {
            return TypeManagement.User.LIST;
        }

        @Override
        public Object execute(MessageContent.Message message) {
            HeaderDto.RequestHeader header = createRequestHeader(TypeManagement.User.LIST);

            try {
                // 사용자 목록 요청은 data가 필요 없음
                String json = mapper.writeValueAsString(new RequestDto(header, null));
                log.debug("사용자 목록 요청 JSON 생성: {}", json);
                return json;
            } catch (JsonProcessingException e) {
                log.error("사용자 목록 요청 JSON 변환 실패", e);
                throw new RuntimeException("사용자 목록 요청 생성 실패", e);
            }
        }
    }

    /**
     * 사용자 목록 응답 Command
     * 서버로부터 받은 사용자 목록 응답을 DTO로 변환
     */
    public static class UserListReceiveCommand extends ReceiveCommand {
        private static final Logger log = LoggerFactory.getLogger(UserListReceiveCommand.class);

        @Override
        public String getType() {
            return TypeManagement.User.LIST_SUCCESS;
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

                // Data 파싱 - UserListResponse는 @JsonValue로 List<UserInfo>를 직접 반환
                List<UserDto.UserInfo> userList = mapper.treeToValue(
                    responseMessage.data(),
                    mapper.getTypeFactory().constructCollectionType(List.class, UserDto.UserInfo.class)
                );

                UserDto.UserListResponse data = new UserDto.UserListResponse(userList);

                log.debug("사용자 목록 수신 - 사용자 수: {}", userList != null ? userList.size() : 0);

                return new ResponseDto(header, data);
            } catch (JsonProcessingException e) {
                log.error("사용자 목록 응답 파싱 실패", e);
                throw new RuntimeException("사용자 목록 응답 처리 실패", e);
            }
        }
    }
}
