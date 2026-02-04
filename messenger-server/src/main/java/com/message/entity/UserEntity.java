package com.message.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserEntity {
    private final String userId;
    private String name;
    private String passWord;
}
