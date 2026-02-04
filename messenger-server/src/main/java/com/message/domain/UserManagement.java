package com.message.domain;

import com.message.entity.UserEntity;
import com.message.exception.custom.user.LoginInvalidRequestException;
import com.message.exception.custom.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserManagement {
    private static final Map<String, UserEntity> users = new ConcurrentHashMap<>();

    static {
        UserEntity user = new UserEntity("marco", "마르코", "nhnacademy123");
        users.put(user.getUserId(), user);
    }

    private UserManagement() {
    }

    public static void addUser(UserEntity user){
        if(users.containsKey(user.getUserId())){
            throw new LoginInvalidRequestException("이미 존재하는 아이디입니다.");
        }

        users.put(user.getUserId(), user);
    }

    public static UserEntity getUser(String userId){
        if(!users.containsKey(userId)){
            log.warn("존재하지 않는 유저 정보 요청 - userId: {}", userId);
            throw new UserNotFoundException("존재하지 않는 유저 정보 요청입니다.");
        }

        return users.get(userId);
    }

    public static UserManagement createUserManagement() {
        return new UserManagement();
    }
}