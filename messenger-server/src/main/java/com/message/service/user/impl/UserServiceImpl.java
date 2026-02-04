package com.message.service.user.impl;

import com.message.domain.SessionManagement;
import com.message.domain.UserManagement;
import com.message.dto.data.impl.UserDto;
import com.message.entity.UserEntity;
import com.message.service.user.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    @Override
    public List<UserDto.UserInfo> getUserList(){
        List<String> connectedUserIds = SessionManagement.getAllUsers();

        return connectedUserIds.stream()
                .map(id -> {
                    UserEntity userEntity = UserManagement.getUser(id);
                    return new UserDto.UserInfo(id, userEntity.getName(), true);
                })
                .toList();
    }
}
