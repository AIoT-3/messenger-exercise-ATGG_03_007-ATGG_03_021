package com.message.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.message.TypeManagement;
import com.message.cofig.AppConfig;
import com.message.dto.HeaderDto;
import com.message.dto.RequestDto;
import com.message.dto.ResponseDto;
import com.message.dto.data.ResponseDataDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class ClientSession {
    @Setter
    @Getter
    private static String sessionId;

    @Setter
    @Getter
    private static String userId;

    @Setter
    @Getter
    private static long currentRoomId = 1;

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static boolean isAuthenticated() {
        return sessionId != null;
    }

    public static void clear() {
        sessionId = null;
        userId = null;
    }

    public static void connect() {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket(AppConfig.HOST, AppConfig.PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                log.info("서버와 연결되었습니다 - {}:{}", AppConfig.HOST, AppConfig.PORT);
            }
        } catch (IOException e) {
            log.error("서버와의 연결에 실패했습니다.", e);
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            log.error("[에러] 연결이 끊겼습니다.", e);
        }
    }

    public static void send(RequestDto requestDto) {
        try {
            String json = objectMapper.writeValueAsString(requestDto);
            out.println(json);
            log.debug("전송된 요청: {}", json);
        } catch (JsonProcessingException e) {
            log.error("요청을 직렬화하는데에 실패하였습니다.", e);
            throw new RuntimeException(e);
        }
    }

    public static ResponseDto receive() {
        try {
            String json = in.readLine();
            if (json == null) {
                throw new IOException("서버에 의해 연결이 끊겼습니다.");
            }
            log.debug("전달받은 응답: {}", json);

            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode headerNode = rootNode.get("header");
            String type = headerNode.get("type").asText();

            HeaderDto.ResponseHeader header = objectMapper.treeToValue(headerNode, HeaderDto.ResponseHeader.class);
            
            Class<? extends ResponseDataDto> dataClass = TypeManagement.responseDataDataDtoClassMap.get(type);
            ResponseDataDto data = null;
            if (dataClass != null && rootNode.has("data")) {
                data = objectMapper.treeToValue(rootNode.get("data"), dataClass);
            }

            return new ResponseDto(header, data);
        } catch (IOException e) {
            log.error("응답을 받는데에 실패하였습니다.", e);
            throw new RuntimeException(e);
        }
    }
}