package com.message.dispatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.message.dto.ErrorResponse;
import com.message.exception.GlobalExceptionHandler;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import com.message.handler.Handler;
import com.message.handler.HandlerFactory;
import com.message.mapper.dispatch.DispatchMapper;
import com.message.mapper.dispatch.impl.DispatchMapperImpl;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageDispatcher {
    private final DispatchMapper dispatchMapper = new DispatchMapperImpl();
    private final GlobalExceptionHandler globalExceptionHandler= new GlobalExceptionHandler();

    public String dispatch(String jsonBody) {
        try {
            // 1. JSON을 트리 구조로 먼저 읽습니다. (전체 변환 X)
            JsonNode rootNode;
            try {
                rootNode = dispatchMapper.readTree(jsonBody);
            } catch (JsonProcessingException e) {
                throw new ObjectMappingFailException("요청 매핑 실패");
            }

            // 2. Header 영역에서 필요한 정보 추출
            JsonNode headerNode = rootNode.path("header");
            String type = headerNode.path("type").asText();
            String sessionId = headerNode.path("sessionId").asText();

            // 3. Data 영역만 따로 떼어냅니다.
            JsonNode dataNode = rootNode.path("data");

            // 4. Type에 맞는 핸들러 선택
            Handler handler = HandlerFactory.getMessageHandler(type);
            if (handler == null) {
                // TODO 고민해라...
                throw new AppException("", "알 수 없는 타입: " + type);
            }

            // 5. 핸들러에게 'sessionId'와 'data 노드'를 넘겨서 처리 요청
            Object result = handler.execute(dataNode.textValue());

            // 6. 결과 반환 (성공 응답 생성)
            return dispatchMapper.toResult(result);

        } catch (Exception e) {
            ErrorResponse errorResponse = globalExceptionHandler.exceptionHandler(e);
            return dispatchMapper.toError(errorResponse);
        }
    }
}