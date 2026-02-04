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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientSession {
    private static final Logger log = LoggerFactory.getLogger(ClientSession.class);

    private static String sessionId;
    private static String userId;
    private static long currentRoomId = 1;

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String sessionId) {
        ClientSession.sessionId = sessionId;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        ClientSession.userId = userId;
    }

    public static long getCurrentRoomId() {
        return currentRoomId;
    }

    public static void setCurrentRoomId(long currentRoomId) {
        ClientSession.currentRoomId = currentRoomId;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static PrintWriter getOut() {
        return out;
    }

    public static BufferedReader getIn() {
        return in;
    }

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
                // 인코딩을 UTF-8로 명시하여 한글 깨짐 방지 및 문자열 길이 일관성 확보
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
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

    public static OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }
}