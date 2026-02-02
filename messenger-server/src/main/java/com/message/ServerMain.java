package com.message;

import com.message.thread.runnable.MessageServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerMain {
    public static void main(String[] args) {
        MessageServer messageServer = new MessageServer();
        Thread thread = new Thread(messageServer);
        thread.start();
        log.info("서버를 열었습니다.");


        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}