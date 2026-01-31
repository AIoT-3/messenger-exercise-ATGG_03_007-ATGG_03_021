package com.message.thread.executable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.message.domain.FilterManagement;
import com.message.domain.SessionManagement;
import com.message.dto.ErrorDto;
import com.message.dto.HeaderDto;
import com.message.exception.GlobalExceptionHandler;
import com.message.exception.custom.handler.HandlerNotFoundException;
import com.message.exception.custom.mapper.ObjectMappingFailException;
import com.message.handler.Handler;
import com.message.handler.HandlerFactory;
import com.message.mapper.dispatch.DispatchMapper;
import com.message.mapper.dispatch.impl.DispatchMapperImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

@Slf4j
public class MessageDispatcher implements Executable {
    private final DispatchMapper dispatchMapper = new DispatchMapperImpl();
    private final GlobalExceptionHandler globalExceptionHandler= new GlobalExceptionHandler();
    private final Socket socket;

    public MessageDispatcher(Socket socket){
        this.socket = socket;
    }

    @Override
    public void execute() {
        try {
//            Socket accept = serverSocket.accept();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
                InetAddress inetAddress = socket.getInetAddress();
                log.debug("ip: {}, port: {}", inetAddress.getAddress(), socket.getPort());

                // 1. 첫 줄에서 message-length 읽기
                String lengthLine = reader.readLine();
                if (lengthLine == null || !lengthLine.startsWith("message-length:")) {
                    throw new IllegalArgumentException("Invalid message format: missing message-length");
                }
                int length = Integer.parseInt(lengthLine.substring("message-length:".length()).trim());

                // 2. 해당 길이만큼 읽기
                char[] buffer = new char[length];
                int totalRead = 0;
                while (totalRead < length) {
                    int read = reader.read(buffer, totalRead, length - totalRead);
                    if (read == -1) {
                        throw new IOException("Unexpected end of stream");
                    }
                    totalRead += read;
                }

                String result = dispatch(new String(buffer));

                writer.println(result);
                writer.flush();
            }
        } catch (IOException e) {
            log.error("[스레드 에러] - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String dispatch(String jsonBody) {
        try {
            log.debug("jsonBody:{}",jsonBody);
            // 1. JSON을 트리 구조로 먼저 읽습니다. (전체 변환 X)
            JsonNode rootNode;
            rootNode = dispatchMapper.readTree(jsonBody);

            // 2. Header 영역에서 필요한 정보 추출
            HeaderDto.RequestHeader requestHeader;
            requestHeader = dispatchMapper.requestHeaderPasser(rootNode);

            FilterManagement.filterChain.doFilter(requestHeader);

            // 3. Data 영역만 따로 떼어냅니다.
            JsonNode dataNode = rootNode.path("data");

            // 4. Type에 맞는 핸들러 선택
            Handler handler = HandlerFactory.getMessageHandler(requestHeader.type());
            if (handler == null) {
                log.warn("[핸들러 요청] 존재하지 않는 핸들러 요청 - method:{}", requestHeader.type());
                throw new HandlerNotFoundException("[알 수 없는 타입] type: " + requestHeader.type());
            }

            // 5. 핸들러에게 'sessionId'와 'data 노드'를 넘겨서 처리 요청
            Object result = handler.execute(dataNode.textValue());

            // 6. 결과 반환 (성공 응답 생성)
            return dispatchMapper.toResult(result);

        } catch (Exception e) {
            log.error("[메시지 디스패치] 처리 중 오류 발생");
            ErrorDto errorDto = globalExceptionHandler.exceptionHandler(e);
            try {
                return dispatchMapper.toError(errorDto);
            } catch (JsonProcessingException ex) {
                log.error("[오류 메시지 디스패치] JSON 변환 중 치명적인 오류 발생");
                throw new ObjectMappingFailException(ex.getMessage());
            }
        } finally {
            try {
                if(Objects.nonNull(socket)){
                    socket.close();
                    log.debug("client 정상종료");

                    //client제거
                    if(SessionManagement.isExisted("id")) {
                        SessionManagement.deleteSession("id");
                    }
                }
            } catch (IOException e) {
                log.error("error-client-close : {}",e.getMessage(),e);
            }
        }
    }

}