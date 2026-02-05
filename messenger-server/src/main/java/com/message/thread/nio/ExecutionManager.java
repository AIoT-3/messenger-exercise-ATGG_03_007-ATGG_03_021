package com.message.thread.nio;

import com.fasterxml.jackson.databind.JsonNode;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.dto.data.RequestDataDto;
import com.message.filter.FilterChain;
import com.message.mapper.dispatch.DispatchMapper;
import com.message.mapper.dispatch.impl.DispatchMapperImpl;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

@Slf4j
public class ExecutionManager {
    private final DispatchMapper dispatchMapper = new DispatchMapperImpl();
    private final FilterChain filterChain = FilterChain.getFilterChain();
    private final Selector selector; // OP_WRITE 등록을 위해 필요

    public ExecutionManager(Selector selector) {
        this.selector = selector;
    }

    public void dispatch(String json, SocketChannel channel) {
        try {
            // 1. JSON을 트리 구조로 먼저 읽습니다. (전체 변환 X)
            JsonNode rootNode = dispatchMapper.readTree(json);

            // 2. Header 영역에서 필요한 정보 추출
            HeaderDto.RequestHeader requestHeader = dispatchMapper.requestHeaderParser(rootNode);

            RequestDataDto requestData = dispatchMapper.requestDataParser(requestHeader.type(), rootNode);

            RequestDto request = dispatchMapper.requestParser(requestHeader, requestData);

            filterChain.doFilter(request);

            // 2. 해당 타입에 맞는 스레드 풀 선택
            ExecutorService pool = ThreadPoolFactory.getThreadPool(requestHeader.type());

            // 3. 스레드 풀에 작업 위임 (비동기)
            pool.submit(() -> new HandlerExecutable(channel, selector, requestHeader, requestData).execute());

        } catch (Exception e) {
            log.error("JSON 파싱 에러: {}", e.getMessage());
        }
    }
}