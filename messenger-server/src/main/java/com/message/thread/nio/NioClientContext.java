package com.message.thread.nio;

import com.message.cofig.AppConfig;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

@Slf4j
public class NioClientContext {
    private static final int BUFFER_SIZE = 8192;
    private final SocketChannel clientChannel;

    // 수신(Read)용 버퍼
    private final ByteBuffer readBuffer;
    // 송신(Write)용 버퍼 (응답 데이터를 임시 저장)
    private ByteBuffer writeBuffer;

    private int targetBodyLength = -1;
    private int headerFullLength = -1; // "message-length:186\n" 전체의 바이트 길이

    public NioClientContext(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
        this.readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    public int readFromChannel() throws IOException {
        return clientChannel.read(readBuffer);
    }

    /**
     * 바이트 단위로 직접 검사하여 성능 최적화
     */
    public boolean hasCompleteMessage() {
        readBuffer.flip(); // 검사를 위해 읽기 모드 전환
        try {
            // 1. 헤더가 파싱되지 않았다면 파싱 시도
            if (targetBodyLength == -1) {
                int newlinePos = findNewline(readBuffer);
                if (newlinePos == -1) return false; // 아직 \n이 안 옴

                // 헤더 파싱 및 길이 저장
                parseHeader(newlinePos);
                // headerFullLength = (0부터 \n까지의 거리) + 1(\n 자체)
                this.headerFullLength = newlinePos + 1;
            }

            // 2. 본문이 다 왔는지 체크
            // 현재 버퍼에 담긴 총량(remaining)이 (헤더길이 + 본문길이) 이상인지 확인
            if (readBuffer.remaining() >= headerFullLength + targetBodyLength) {
                return true;
            }
            return false;
        } finally {
            // 핵심: 검사가 끝났으면 다시 데이터를 채울 수 있게 compact()
            // hasCompleteMessage가 false면 다음 데이터를 뒤에 이어 붙이고,
            // true면 popCompleteMessage 호출 후 compact가 실행됨
            readBuffer.compact();
        }
    }

    /**
     * 버퍼에서 \n의 위치를 찾는 헬퍼 메서드
     */
    private int findNewline(ByteBuffer buf) {
        for (int i = buf.position(); i < buf.limit(); i++) {
            if (buf.get(i) == '\n') return i;
        }
        return -1;
    }

    private void parseHeader(int newlinePos) {
        // 버퍼의 시작부터 \n 직전까지의 바이트를 추출
        byte[] headerBytes = new byte[newlinePos];
        readBuffer.get(headerBytes);
        readBuffer.get(); // \n (1바이트) 건너뛰기 -> 이제 포지션은 본문의 시작점

        String header = new String(headerBytes, StandardCharsets.UTF_8);
        if (header.startsWith(AppConfig.MESSAGE_LENGTH)) {
            // "message-length:186" -> "186" 추출
            String lengthStr = header.substring(AppConfig.MESSAGE_LENGTH.length()).trim();
            this.targetBodyLength = Integer.parseInt(lengthStr);
        }

        // 다시 처음으로 되돌려놔야 hasCompleteMessage의 remaining() 계산이 정확함
        readBuffer.position(0);
    }

    public String popCompleteMessage() {
        readBuffer.flip(); // 읽기 모드

        // 1. 헤더 영역 통과 (\n 다음으로 포지션 이동)
        readBuffer.position(headerFullLength);

        // 2. 정해진 길이만큼 본문 데이터만 싹둑 자르기
        byte[] bodyBytes = new byte[targetBodyLength];
        readBuffer.get(bodyBytes);

        String message = new String(bodyBytes, StandardCharsets.UTF_8);

        // 3. 상태 초기화 (다음 메시지를 위해)
        targetBodyLength = -1;
        headerFullLength = -1;

        // 4. 버퍼에 남은 데이터가 있다면 앞으로 밀기
        readBuffer.compact();

        return message;
    }

    /**
     * 응답 전송용 버퍼 세팅
     */
    public void setResponse(String response) {
        byte[] body = response.getBytes(StandardCharsets.UTF_8);
        String header = AppConfig.MESSAGE_LENGTH + body.length + "\n";
        byte[] head = header.getBytes(StandardCharsets.UTF_8);

        this.writeBuffer = ByteBuffer.allocate(head.length + body.length);
        this.writeBuffer.put(head);
        this.writeBuffer.put(body);
        this.writeBuffer.flip(); // 쓰기 모드 준비 완료
    }

    public boolean writeToChannel() throws IOException {
        if (writeBuffer == null) return true;

        clientChannel.write(writeBuffer);
        return !writeBuffer.hasRemaining(); // 다 보냈으면 true
    }
}