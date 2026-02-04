package com.message.mapper.response.impl;

import com.message.TypeManagement;
import com.message.dto.ResponseDto;
import com.message.dto.data.impl.UserDto;
import com.message.mapper.response.AbstractResponseMapper;

public class UserListResponseMapperImpl extends AbstractResponseMapper {
    @Override
    public String getClassName() {
        return UserDto.UserListResponse.class.getName();
    }

    @Override
    protected String getErrorMessage() {
        return "[유저 리스트] 서버 문제로 결과가 null입니다.";
    }

    @Override
    protected boolean isInstanceof(Object o) {
        return o instanceof UserDto.UserListResponse;
    }

    @Override
    protected ResponseDto createResponseDto(Object o) {
        return new ResponseDto(
                createHeader(TypeManagement.User.LIST_SUCCESS),
                (UserDto.UserListResponse) o
        );
    }
}
