package com.message.runnable;

import com.message.cofig.AppConfig;
import com.message.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

/**
 * 서버로부터 메시지를 수신하는 스레드
 * 수신된 JSON 메시지를 Subject에 전달하여 처리
 */
public class ReceivedMessageClient implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ReceivedMessageClient.class);

    private final Socket socket;
    private final Subject subject;
    private volatile boolean running = true;

    public ReceivedMessageClient(Socket socket, Subject subject) {
        if (Objects.isNull(socket) || Objects.isNull(subject)) {
            throw new IllegalArgumentException("Socket과 Subject는 null일 수 없습니다.");
        }

        this.socket = socket;
        this.subject = subject;
    }

    @Override
    public void run() {
        log.info("메시지 수신 스레드 시작");

        try (java.io.InputStream is = socket.getInputStream()) {
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    // 1. 헤더 읽기
                    java.io.ByteArrayOutputStream headerBuffer = new java.io.ByteArrayOutputStream();
                    int b;
                    boolean headerComplete = false;
                    while ((b = is.read()) != -1) {
                        if (b == '\n') {
                            headerComplete = true;
                            break;
                        }
                        headerBuffer.write(b);
                    }

                    if (!headerComplete) {
                         if (headerBuffer.size() > 0) {
                             log.info("서버와의 연결이 종료되었습니다 (Incomplete header).");
                         } else {
                             // EOF immediately
                             log.info("서버와의 연결이 종료되었습니다.");
                         }
                         break;
                    }

                    String lengthLine = headerBuffer.toString(java.nio.charset.StandardCharsets.UTF_8).trim();
                    if (lengthLine.isEmpty()) {
                        continue;
                    }

                    if (!lengthLine.startsWith(AppConfig.MESSAGE_LENGTH)) {
                        log.warn("잘못된 헤더 형식: {}", lengthLine);
                        // 동기화를 잃었을 수 있음. 복구 전략이 필요하지만, 여기서는 일단 무시하거나 연결 종료를 고려해야 함.
                        // 우선은 다음 바이트들을 계속 읽어서 줄바꿈을 찾는 방식으로 갈 수도 있지만,
                        // 프로토콜이 깨졌으므로 연결을 끊는 게 안전할 수 있음.
                        // 여기서는 로그만 남기고 일단 진행해봄 (다음 메시지 처리가 안될 가능성 높음)
                        continue;
                    }

                    int length = Integer.parseInt(lengthLine.substring(AppConfig.MESSAGE_LENGTH.length()).trim());
                    log.debug("{}{}", AppConfig.MESSAGE_LENGTH, length);

                    // 2. 바이트 단위로 Body 읽기
                    byte[] bodyBuffer = new byte[length];
                    int totalRead = 0;
                    while (totalRead < length) {
                        int read = is.read(bodyBuffer, totalRead, length - totalRead);
                        if (read == -1) break;
                        totalRead += read;
                    }

                    String jsonMessage = new String(bodyBuffer, java.nio.charset.StandardCharsets.UTF_8);

                    if (!jsonMessage.trim().isEmpty()) {
                        log.debug("메시지 수신: {}", jsonMessage);
                        processMessage(jsonMessage);
                    }

                    // 서버가 println으로 보냈다면 뒤에 \n이 남아있을 수 있음.
                    // 현재 MessageDispatcher는 writer.println(result)를 하므로 \n이 추가됨.
                    // 하지만 바이트 길이(responseBytes.length)는 result.getBytes()로 계산하므로 \n은 포함되지 않음.
                    // 따라서 스트림에는 Body 뒤에 \n이 남아있게 됨. 이를 소비해야 함.
                    // 단, Server의 writer.println이 \r\n을 쓸 수도 있고 \n을 쓸 수도 있음.
                    // 안전하게 하기 위해 공백문자(\r, \n)을 건너뛰는 로직이 필요할 수 있음.
                    // 하지만 여기서는 MessageDispatcher가 println을 쓰므로, 다음 read()에서 \n이 읽힐 것임.
                    // 그런데 다음 루프의 "헤더 읽기"에서 이 \n을 읽게 되면 빈 줄로 처리되어 무시되거나(lengthLine.isEmpty() check),
                    // 헤더 파싱 에러가 날 수 있음.
                    // 가장 깔끔한 건 서버가 body 딱 그만큼만 보내거나, 클라이언트가 다음 토큰을 찾을 때 공백을 무시하는 것.

                    // 서버의 MessageDispatcher 수정 내용을 보면:
                    // writer.println("message-length: " + responseBytes.length);
                    // writer.println(result);
                    // 이렇게 되어 있음. println은 개행을 붙임.
                    // message-length 줄 뒤의 개행은 위에서 읽음.
                    // result 뒤의 개행은 아직 안 읽음.

                    // 남은 개행 문자 소비 (Optional, but recommended given PrintWriter behavior)
                    // peek 기능을 쓸 수 없으니, 다음 루프의 헤더 읽기에서 trim() 된 라인이 빈 문자열이면 무시하는 로직으로 처리됨.
                    // 위 코드에 if (lengthLine.isEmpty()) continue; 가 있으므로,
                    // Body 뒤의 \n (또는 \r\n) 은 다음 루프에서 빈 줄로 읽히고 무시될 것임.

                } catch (SocketException e) {
                    if (running) {
                        log.error("소켓 에러: {}", e.getMessage());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            if (running) {
                log.error("메시지 수신 중 오류 발생: {}", e.getMessage(), e);
            }
        } finally {
            log.info("메시지 수신 스레드 종료");
        }
    }

    /**
     * 수신된 메시지를 Subject에 전달
     */
    private void processMessage(String message) {
        try {
            subject.receiveMessage(message);
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 수신 스레드 중지
     */
    public void stop() {
        running = false;
    }
}