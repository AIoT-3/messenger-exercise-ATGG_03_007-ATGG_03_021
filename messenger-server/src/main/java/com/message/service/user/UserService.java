package com.message.service.user;

import com.message.dto.data.impl.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto.UserInfo> getUserList();
}
