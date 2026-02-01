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
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class MessageDispatcher implements Executable {
    private final DispatchMapper dispatchMapper = new DispatchMapperImpl();
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    private final Socket socket;

    public MessageDispatcher(Socket socket) {
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
                log.debug("읽어야 할 본문 길이: {}", length);

                // TODO 수정사항
                // 본문 읽기 (바이트 단위가 아닌 문자 단위이므로)
                StringBuilder bodyBuilder = new StringBuilder();
                int charCode;

                // 제이슨은 { 로 시작하므로 첫 문자가 나올 때까지 무시하거나 선언된 길이만큼 하나씩 읽기
                for(int i = 0; i < length; i++) {
                    charCode = reader.read();
                    if(charCode == -1) {
                        break;
                    }
                    bodyBuilder.append((char)charCode);
                }

                String jsonBody = bodyBuilder.toString().trim();
                log.debug("수신된 jsonBody: {}", jsonBody);

                if(jsonBody.isEmpty()) {
                    log.error("데이터의 본문이 비어있습니다.");
                    return;
                }

                String result = dispatch(jsonBody);
                log.debug("최종 전송할 응답 JSON: {}", result); // TODO 응답 보내기도 전에 닫아버리는 것 같아서 테스트


                // TODO 수정사항 (재민)
                // 요구사항 명세서에 길이 헤더 (Length Line) 첫 줄에 보내라고 함
                byte[] responseBytes = result.getBytes(StandardCharsets.UTF_8);

                writer.println("message-length: " + responseBytes.length); // 길이 헤더 전송
                writer.println(result); // 페이로드(제이슨 데이터) 전송
                writer.flush();

                // 2. 해당 길이만큼 읽기
//                char[] buffer = new char[length];
//                int totalRead = 0;
//                while (totalRead < length) {
//                    int read = reader.read(buffer, totalRead, length - totalRead);
//                    if (read == -1) {
//                        throw new IOException("Unexpected end of stream");
//                    }
//                    totalRead += read;
//                }
//
//                String result = dispatch(new String(buffer));
//
//                writer.println(result);
//                writer.flush();
            }
        } catch (IOException e) {
            log.error("[스레드 에러] - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String dispatch(String jsonBody) {
        try {
            log.debug("jsonBody:{}", jsonBody);
            // 1. JSON을 트리 구조로 먼저 읽습니다. (전체 변환 X)
            JsonNode rootNode;
            rootNode = dispatchMapper.readTree(jsonBody);

            // 2. Header 영역에서 필요한 정보 추출
            HeaderDto.RequestHeader requestHeader;
            requestHeader = dispatchMapper.requestHeaderPasser(rootNode);

            // TODO 수정사항 (재민)
            // FilterManagement.filterChain.doFilter(requestHeader);
            FilterManagement.getChain().doFilter(requestHeader);

            // 3. Data 영역만 따로 떼어냅니다.
            JsonNode dataNode = rootNode.path("data");

            // 4. Type에 맞는 핸들러 선택
            Handler handler = HandlerFactory.getMessageHandler(requestHeader.type());
            if (handler == null) {
                log.warn("[핸들러 요청] 존재하지 않는 핸들러 요청 - method:{}", requestHeader.type());
                throw new HandlerNotFoundException("[알 수 없는 타입] type: " + requestHeader.type());
            }

            // 5. 핸들러에게 'sessionId'와 'data 노드'를 넘겨서 처리 요청
            // Object result = handler.execute(dataNode.textValue());

            // 만약 클라가 {"data": {"userId": "test"}} 이렇게 객체를 보내면 Jackson의 textValue()는 문자열 노드가 아닐 경우 널 리턴
            // 핸들러에 널이 전달되니까 로직 꼬이고 세션 필터랑 핸들러에서 에러 나는듯
            // 객체 전체를 문자열로 넘기고 싶다면? dataNode.toString() 사용하거나 dataNode 자체를 핸들러에게 넘기기
            // 만약 데이터가 단순 문자열이 아니라 제이슨 객체라면 toString()
            Object result = handler.execute(dataNode.isTextual() ? dataNode.textValue() : dataNode.toString());

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