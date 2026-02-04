package com.message.thread.executable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.dto.data.RequestDataDto;
import com.message.dto.data.impl.ErrorDto;
import com.message.exception.GlobalExceptionHandler;
import com.message.exception.custom.handler.HandlerNotFoundException;
import com.message.filter.FilterChain;
import com.message.handler.Handler;
import com.message.handler.HandlerFactory;
import com.message.mapper.dispatch.DispatchMapper;
import com.message.mapper.dispatch.impl.DispatchMapperImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MessageDispatcher implements Executable {
    private final DispatchMapper dispatchMapper = new DispatchMapperImpl();
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    private final Socket socket;
    private final FilterChain filterChain = FilterChain.getFilterChain();

    public MessageDispatcher(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void execute() {
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            // 연결을 유지하면서 메시지를 계속 처리
            while (!socket.isClosed()) {
                // 1. 헤더 읽기 (InputStream에서 직접 읽음)
                ByteArrayOutputStream headerBuffer = new ByteArrayOutputStream();
                int b;
                // \n(10)이 나올 때까지 읽음
                while ((b = is.read()) != -1) {
                    if (b == '\n') {
                        break;
                    }
                    headerBuffer.write(b);
                }

                // 연결 종료 감지
                if (b == -1) {
                    log.info("클라이언트 연결 종료");
                    break;
                }

                String lengthLine = headerBuffer.toString(StandardCharsets.UTF_8).trim();
                log.debug("lengthLine: {}", lengthLine);

                // 빈 헤더는 무시하고 다음 메시지 대기
                if (lengthLine.isEmpty()) {
                    continue;
                }

                if (!lengthLine.startsWith("message-length:")) {
                    log.warn("Invalid message format: {}", lengthLine);
                    continue;
                }

                int length = Integer.parseInt(lengthLine.substring("message-length:".length()).trim());
                log.debug("읽어야 할 본문 길이 (Bytes): {}", length);

                // 2. 바이트 단위로 정확히 읽기
                byte[] bodyBuffer = new byte[length];
                int totalRead = 0;
                while (totalRead < length) {
                    int read = is.read(bodyBuffer, totalRead, length - totalRead);
                    if (read == -1) break;
                    totalRead += read;
                }

                String jsonBody = new String(bodyBuffer, StandardCharsets.UTF_8);
                log.debug("수신된 jsonBody: {}", jsonBody);

                if (jsonBody.isEmpty()) {
                    log.error("데이터의 본문이 비어있습니다.");
                    continue;
                }

                String result = dispatch(jsonBody);
                log.debug("최종 전송할 응답 JSON: {}", result);

                byte[] responseBytes = result.getBytes(StandardCharsets.UTF_8);

                // 헤더: message-length:길이\n 형식으로 전송 (클라이언트와 프로토콜 일치)
                String header = "message-length:" + responseBytes.length + "\n";
                os.write(header.getBytes(StandardCharsets.UTF_8)); // 헤더 전송
                os.write(responseBytes); // 페이로드(제이슨 데이터) 전송
                os.flush();
            }
        } catch (IOException e) {
            log.error("[스레드 에러] - {}", e.getMessage());
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                log.error("소켓 종료 실패: {}", e.getMessage());
            }
        }
    }

    private String dispatch(String jsonBody) {
        try {
            // 1. JSON을 트리 구조로 먼저 읽습니다. (전체 변환 X)
            JsonNode rootNode = dispatchMapper.readTree(jsonBody);

            // 2. Header 영역에서 필요한 정보 추출
            HeaderDto.RequestHeader requestHeader = dispatchMapper.requestHeaderParser(rootNode);

            RequestDataDto requestData = dispatchMapper.requestDataParser(requestHeader.type(), rootNode);

            RequestDto request = dispatchMapper.requestParser(requestHeader, requestData);

             filterChain.doFilter(request);

            // 3. Data 영역만 따로 떼어냅니다.

            // 4. Type에 맞는 핸들러 선택
            Handler handler = HandlerFactory.getMessageHandler(requestHeader.type());
            if (handler == null) {
                log.warn("[핸들러 요청] 존재하지 않는 핸들러 요청 - method:{}", requestHeader.type());
                throw new HandlerNotFoundException("[알 수 없는 타입] type: " + requestHeader.type());
            }

            // 5. 핸들러에게 'header' 와 'data 노드'를 넘겨서 처리 요청
            Object result = handler.execute(requestHeader, requestData);

            // 6. 결과 반환 (성공 응답 생성)
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

            // TODO 수정사항 (재민)
            // finally 블록에서 소켓 클로즈 먼저 해버려서, 그 뒤에 나오는 writer.println(result)는 이미 닫힌 문에다 프린트 하는 격이 됨
            // 이미 execute() 메서드 시작 부분에 try-with-resources 구문 있으니, 이 블록 끝나면 알아서 writer 닫고 연결된 소켓도 닫아줌
//        } finally {
//            try {
//                if (Objects.nonNull(socket)) {
//                    socket.close();
//                    log.debug("client 정상종료");
//
//                    //client제거
//                    if (SessionManagement.isExisted("id")) {
//                        SessionManagement.deleteSession("id");
//                    }
//                }
//            } catch (IOException e) {
//                log.error("error-client-close : {}", e.getMessage(), e);
//            }
//        }
        }
    }
}