package com.message.cofig;

public class AppConfig {
    public static final String HOST = "127.0.0.1"; // "10.69.24.118";
    public static final int PORT = 8080;
    public static final String PROTOCOL = "http";

    public static final String MESSAGE_LENGTH = "message-length:";

    public static String getBaseUrl() {
        return String.format("%s://%s:%d", PROTOCOL, HOST, PORT);
    }
}
