package com.message.handler.impl;

import com.message.TypeManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.UserDto;
import com.message.handler.Handler;
import com.message.service.user.UserService;
import com.message.service.user.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UserListHandler implements Handler {
    private final UserService userService = new UserServiceImpl();

    @Override
    public String getMethod() {
        return TypeManagement.User.LIST;
    }

    @Override
    public Object execute(HeaderDto.RequestHeader header, RequestDataDto data) {
        List<UserDto.UserInfo> userList = userService.getUserList();

        log.debug("[UserListHandler] 접속자 목록 생성 완료");
        return new UserDto.UserListResponse(userList);
    }
}
