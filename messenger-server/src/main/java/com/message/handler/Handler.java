package com.message.handler;

public interface Handler {
    String getMethod();
    String execute(String value);

    default boolean validate(String method){
        return getMethod().equals(method);
    }
}