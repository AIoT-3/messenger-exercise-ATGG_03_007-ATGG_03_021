package com.message;

import com.message.thread.nio.MessageNioServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerNioMain {
    public static void main(String[] args) {
        MessageNioServer messageNioServer = new MessageNioServer();
        Thread thread = new Thread(messageNioServer);
        thread.start();
        log.info("NIO 기반 서버가 열렸습니다.");

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}