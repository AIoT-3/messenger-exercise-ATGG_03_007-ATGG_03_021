package com.message.connection;

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

/**
 * 서버와의 소켓 연결 및 I/O를 전담하는 싱글톤 클래스.
 * 세션 상태(userId, sessionId 등)는 {@link com.message.session.ClientSession}이 관리한다.
 */
public class ClientConnection {
    private static final Logger log = LoggerFactory.getLogger(ClientConnection.class);

    private static final ClientConnection INSTANCE = new ClientConnection();

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private ClientConnection() {}

    public static ClientConnection getInstance() {
        return INSTANCE;
    }

    public void connect() {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket(AppConfig.HOST, AppConfig.PORT);
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                log.info("서버와 연결되었습니다 - {}:{}", AppConfig.HOST, AppConfig.PORT);
            }
        } catch (IOException e) {
            log.error("서버와의 연결에 실패했습니다.", e);
            throw new RuntimeException("서버 연결 실패: " + AppConfig.HOST + ":" + AppConfig.PORT, e);
        }
    }

    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            log.error("[에러] 연결이 끊겼습니다.", e);
        }
    }

    public void send(RequestDto requestDto) {
        try {
            String json = objectMapper.writeValueAsString(requestDto);
            out.println(json);
            log.debug("전송된 요청: {}", json);
        } catch (IOException e) {
            log.error("요청을 직렬화하는데에 실패하였습니다.", e);
            throw new RuntimeException("요청 직렬화 실패", e);
        }
    }

    public ResponseDto receive() {
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
            throw new RuntimeException("응답 수신 실패", e);
        }
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }
}
