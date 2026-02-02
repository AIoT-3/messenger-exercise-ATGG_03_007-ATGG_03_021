package com.message.cofig;

public class AppConfig {
    public static final String HOST = "localhost";
    public static final int PORT = 8080;
    public static final String PROTOCOL = "http";

    public static String getBaseUrl() {
        return String.format("%s://%s:%d", PROTOCOL, HOST, PORT);
    }
}
