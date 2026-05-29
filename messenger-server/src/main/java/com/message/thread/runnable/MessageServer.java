package com.message.thread.runnable;

import com.message.cofig.AppConfig;
import com.message.thread.channel.RequestChannel;
import com.message.thread.executable.MessageDispatcher;
import com.message.thread.pool.WorkerThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class MessageServer implements Runnable {

    private final int port;
    private final ServerSocket serverSocket;
    private final WorkerThreadPool workerThreadPool;
    private final RequestChannel requestChannel;

    private static final Map<String, Socket> clientMap = new ConcurrentHashMap<>();

    public MessageServer() {
        this(AppConfig.PORT);
    }

    public MessageServer(int port) {
        if (port <= 0) {
            throw new IllegalArgumentException("포트 번호는 1 이상이어야 합니다. port=" + port);
        }

        this.port = port;

        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        requestChannel = new RequestChannel();
        workerThreadPool = new WorkerThreadPool(() -> requestChannel.getJob().execute());
    }

    @Override
    public void run() {
        workerThreadPool.start();
        log.info("서버가 시작되었습니다. port={}", port);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket client = serverSocket.accept();
                requestChannel.addJob(new MessageDispatcher(client));
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    log.info("서버 소켓이 종료되어 루프를 빠져나갑니다.");
                    Thread.currentThread().interrupt();
                } else {
                    // 일시적 오류(연결 거절 등)는 로그만 남기고 계속 수락 대기
                    log.error("[서버] 클라이언트 연결 수락 중 오류 발생, 계속 대기합니다: {}", e.getMessage());
                }
            }
        }

        log.info("서버 루프가 종료되었습니다.");
    }

    public static boolean addClient(String id, Socket socket) {
        if (clientMap.containsKey(id)) {
            log.debug("id: {}, aready exist client socket!", id);
            return false;
        }

        clientMap.put(id, socket);
        return true;
    }
}
