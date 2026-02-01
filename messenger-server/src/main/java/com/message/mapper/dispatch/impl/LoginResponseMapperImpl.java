package com.message.mapper.dispatch.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.message.TypeManagement;
import com.message.dto.AuthDto;
import com.message.dto.HeaderDto;
import com.message.dto.ResponseDto;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import com.message.mapper.dispatch.ResponseMapper;

import java.time.OffsetDateTime;
import java.util.Objects;

public class LoginResponseMapperImpl implements ResponseMapper {
    private final ObjectMapper mapper = new ObjectMapper();

    // TODO 수정사항
    // OffsetDateTime을 어떻게 제이슨으로 바꾸는 지 몰라 하는 문제 수정
    // DispatchMapperImpl에는 JavaTimeModule을 등록했는데, LoginResponseMapperImpl 안에도 별도의 ObjectMapper가 있고, 여기에는 설정 안 되어있었음 -> 날짜 관련 모듈 등록 완료
    {
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // 터미널에서 걍 제이슨 한 줄로 보기 싫어서
    }

    // TODO 수정사항
    // < 문제점 >
    // 등록할 때 (Factory): getClassName() 호출하여 "LoginResponse" 문자열을 키로 사용해 맵에 저장함
    // 찾을 때 (DispatchMapper): result.getClass().getName() 호출함. 이때 자바는 클래스의 전체 패키지 경로를 포함한 이름 반환함 (com.message.dto.AuthDto$LoginResponse)
    // com.message.dto.AuthDto$LoginResponse 라는 키로 맵을 뒤졌지만, 정작 저장된 키는 LoginResponse 뿐이니까 아무것도 못 찾아서 널 리턴함
    @Override
    public String getClassName() {
        // return "LoginResponse";
        return AuthDto.LoginResponse.class.getName(); // 실제 응답 객체 클래스의 풀네임을 리턴하도록 수정함 (재민)
    }

    @Override
    public String toResponse(Object result) throws JsonProcessingException {
        if(Objects.isNull(result)){
            throw new ObjectMappingFailException("[로그인] 서버 문제로 결과가 null입니다.");
        }

        if(result instanceof AuthDto.LoginResponse loginResponse){
            // TODO 수정사항 (재민)
            // messageId 자리에 고유한 id(현재 시간의 밀리초로 했음) 추가 -> System.currentTimeMillis()
            HeaderDto.ResponseHeader responseHeader = new HeaderDto.ResponseHeader(TypeManagement.Auth.LOGIN_SUCCESS, true, OffsetDateTime.now(), System.currentTimeMillis());
            ResponseDto<AuthDto.LoginResponse> successResponse = new ResponseDto<>(responseHeader, loginResponse);
            return mapper.writeValueAsString(successResponse);
        }
        throw new ObjectMappingFailException("[매핑 실패] 해당하는 매퍼가 호출되었습니다");
    }
}
