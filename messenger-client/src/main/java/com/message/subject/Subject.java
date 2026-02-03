package com.message.subject;

import com.message.observer.Observer;

public interface Subject {
    void register(EventType eventType, Observer observer);
    void remove(EventType eventType, Observer observer);
    void notifyObservers(EventType eventType, String message);

    default void sendMessage(String message){
        notifyObservers(EventType.SEND,message);
    }

    default void receiveMessage(String message){
        notifyObservers(EventType.RECV,message);
    }

    default void localMessage(String message) {
        notifyObservers(EventType.LOCAL, message);
    }
}