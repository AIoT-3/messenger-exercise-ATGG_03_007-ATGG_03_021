package com.message.thread.nio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.message.TypeManagement;
import com.message.domain.SocketManagement;
import com.message.dto.HeaderDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.ErrorDto;
import com.message.exception.GlobalExceptionHandler;
import com.message.exception.custom.handler.HandlerNotFoundException;
import com.message.handler.Handler;
import com.message.handler.HandlerFactory;
import com.message.mapper.dispatch.DispatchMapper;
import com.message.mapper.dispatch.impl.DispatchMapperImpl;
import com.message.thread.executable.Executable;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

@Slf4j
public class HandlerExecutable implements Executable {
    private final DispatchMapper dispatchMapper = new DispatchMapperImpl();
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    private final SocketChannel channel;
    private final Selector selector;
    private final HeaderDto.RequestHeader requestHeader;
    private final RequestDataDto requestData;

    public HandlerExecutable(SocketChannel channel, Selector selector, HeaderDto.RequestHeader requestHeader, RequestDataDto requestData){
        this.channel = channel;
        this.selector = selector;
        this.requestHeader = requestHeader;
        this.requestData = requestData;
    }

    @Override
    public void execute() {
        String responseMessage = dispatch();
        try {
            // 해당 채널의 '장부(Context)'를 꺼냅니다.
            NioClientContext context = (NioClientContext) channel.keyFor(selector).attachment();

            // 응답 데이터 장전 (헤더 + JSON)
            context.setResponse(responseMessage);

            // Selector에게 쓰기 이벤트(OP_WRITE) 감시 요청
            SelectionKey key = channel.keyFor(selector);
            key.interestOps(SelectionKey.OP_WRITE);

            // 잠든 Selector를 깨워 즉시 전송을 시도하게 함
            selector.wakeup();

        } catch (Exception e) {
            log.error("응답 전송 준비 중 오류: {}", e.getMessage());
        }
    }

    private String dispatch() {
        try {
            Handler handler = HandlerFactory.getMessageHandler(requestHeader.type());
            if (handler == null) {
                log.warn("[핸들러 요청] 존재하지 않는 핸들러 요청 - method:{}", requestHeader.type());
                throw new HandlerNotFoundException("[알 수 없는 타입] type: " + requestHeader.type());
            }

            Object result = handler.execute(requestHeader, requestData);

            if (requestHeader.type().equals(TypeManagement.Auth.LOGIN)) {
                SocketManagement.checkSocket(requestHeader.type(), result, channel.socket());
            } else if (requestHeader.type().equals(TypeManagement.Auth.LOGOUT)) {
                SocketManagement.checkSocket(requestHeader.type(), requestHeader, channel.socket());
            }

            return dispatchMapper.toResult(result);

        } catch (Exception e) {
            log.error("[메시지 디스패치] 처리 중 오류 발생");
            ErrorDto errorDto = globalExceptionHandler.exceptionHandler(e);
            try {
                return dispatchMapper.toError(errorDto);
            } catch (JsonProcessingException ex) {
                log.error("[오류 메시지 디스패치] JSON 변환 중 치명적인 오류 발생");
                return "[오류 메시지 디스패치] JSON 변환 중 치명적인 오류 발생";
            }
        }
    }
}
