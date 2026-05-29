package com.message.service.user.impl;

import com.message.domain.SessionManagement;
import com.message.domain.UserManagement;
import com.message.dto.data.impl.UserDto;
import com.message.entity.UserEntity;
import com.message.service.user.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final SessionManagement sessionManagement;
    private final UserManagement userManagement;

    public UserServiceImpl() {
        this(SessionManagement.getInstance(), UserManagement.getInstance());
    }

    UserServiceImpl(SessionManagement sessionManagement, UserManagement userManagement) {
        this.sessionManagement = sessionManagement;
        this.userManagement = userManagement;
    }

    @Override
    public List<UserDto.UserInfo> getUserList() {
        List<String> connectedUserIds = sessionManagement.getAllUsers();
        return connectedUserIds.stream()
                .map(id -> {
                    UserEntity userEntity = userManagement.getUser(id);
                    return new UserDto.UserInfo(id, userEntity.getName(), true);
                })
                .toList();
    }
}
