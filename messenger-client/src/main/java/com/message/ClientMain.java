package com.message;

import com.message.runnable.MessageClient;

public class ClientMain {
    public static void main(String[] args) {
        MessageClient messageServer = new MessageClient();
        Thread thread = new Thread(messageServer);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
