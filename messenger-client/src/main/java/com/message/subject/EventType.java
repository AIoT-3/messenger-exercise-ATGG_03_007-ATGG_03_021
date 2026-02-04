package com.message.subject;

public enum EventType {
    SEND("송신"),
    RECV("수신"),
    LOCAL("통신X");

    private String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
