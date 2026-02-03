package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.domain.AtomicLongIdManagement;
import com.message.domain.SessionManagement;
import com.message.domain.UserManagement;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.UserDto;
import com.message.entity.UserEntity;
import com.message.exception.custom.user.UserNotFoundException;
import com.message.handler.Handler;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.List;

// TODO 수정사항 (재민)
@Slf4j
public class UserListHandler implements Handler {

    @Override
    public String getMethod() {
        return TypeManagement.User.LIST;
    }

    @Override
    public Object execute(String value) {
        // 세션 메니지먼트에서 유저 아이디만 추출
        List<String> connecteedUserIds = SessionManagement.getAllUsers();

        List<UserDto.UserInfo> userInfos = connecteedUserIds.stream()
                .map(id -> {

                    UserEntity userEntity = UserManagement.getUser(id);
                    return new UserDto.UserInfo(id, userEntity.getName(), true);

                })
                .toList();

        log.debug("[UserListHandler] 접속자 목록 생성 완료: {}");
        return new UserDto.UserListResponse(userInfos);
    }
}
