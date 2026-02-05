package com.message.thread.nio;

import com.message.cofig.AppConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

@Slf4j
public class MessageNioServer implements Runnable{
    private final int port;
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final ExecutionManager executionManager;

    public MessageNioServer() {
        this(AppConfig.PORT);
    }

    public MessageNioServer(int port) {
        this.port = port;
        try {
            // 1. 셀렉터 및 서버 채널 오픈
            this.selector = Selector.open();
            this.serverChannel = ServerSocketChannel.open();

            // 2. 서버 채널 설정 (Non-blocking이 핵심!)
            this.serverChannel.bind(new InetSocketAddress(this.port));
            this.serverChannel.configureBlocking(false);

            // 3. 셀렉터에 Accept(연결 수락) 이벤트 등록
            this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            this.executionManager = new ExecutionManager(selector);

            log.info("NIO 서버가 {} 포트에서 대기 중입니다.", port);
        } catch (IOException e) {
            throw new RuntimeException("서버 초기화 실패", e);
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 이벤트가 발생할 때까지 감시 (여기서만 블로킹)
                if (selector.select() == 0) continue;

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove(); // 처리할 이벤트는 목록에서 즉시 제거

                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        // 여기서 바로 로직을 처리하지 않고, 작업을 던집니다.
                        handleRead(key);
                    } else if (key.isWritable()) {
                        handleWrite(key);
                    }
                }
            } catch (IOException e) {
                log.error("Selector 루프 에러: {}", e.getMessage());
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        client.configureBlocking(false); // 클라이언트 소켓도 Non-blocking 설정

        // 이제 클라이언트 소켓으로부터 데이터를 읽을 준비(READ)를 합니다.
        NioClientContext context = new NioClientContext(client);
        client.register(selector, SelectionKey.OP_READ, context);
        log.info("새로운 클라이언트 연결됨: {}", client.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();

        // 1. 이 채널 전용 버퍼를 꺼냅니다 (Attachment 활용)
        // 없을 경우 handleAccept에서 등록해둔 객체를 가져옵니다.
        NioClientContext context = (NioClientContext) key.attachment();

        try {
            // 2. 데이터를 읽어서 context 내부 버퍼에 쌓습니다.
            int read = context.readFromChannel();

            if (read == -1) { // 클라이언트가 연결을 끊었을 때
                key.cancel();
                client.close();
                return;
            }

            // 3. 메시지가 완성되었는지 확인 (헤더의 길이만큼 다 왔는가?)
            while (context.hasCompleteMessage()) {
                String fullJson = context.popCompleteMessage();

                // 4. 드디어 여기서 Router를 호출하거나 스레드 풀에 Task를 던집니다!
                // 이때 소켓 자체를 넘기기보다, 응답을 보낼 때 필요한 정보와 데이터를 넘깁니다.
                executionManager.dispatch(fullJson, client);
            }

        } catch (IOException e) {
            log.error("읽기 작업 중 에러 발생", e);
            key.cancel();
        }
    }

    // handleWrite 메서드 추가 예시
    private void handleWrite(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        NioClientContext context = (NioClientContext) key.attachment();

        try {
            // 장부에 보낼 데이터가 있다면 전송
            boolean done = context.writeToChannel();

            // 다 보냈다면 다시 읽기 모드로 전환
            if (done) {
                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            log.error("쓰기 작업 중 에러: {}", e.getMessage());
            closeClient(key);
        }
    }

    private void closeClient(SelectionKey key) {
        try {
            key.channel().close();
            key.cancel();
        } catch (IOException ex) {
            log.error("소켓 닫기 실패", ex);
        }
    }

}
