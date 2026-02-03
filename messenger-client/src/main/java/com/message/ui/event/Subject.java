package com.message.ui.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Subject {
    private final Map<EventType, List<Observer>> observers = new HashMap<>();

    public void register(EventType eventType, Observer observer) {
        observers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(observer);
    }

    public void remove(EventType eventType, Observer observer) {
        if (observers.containsKey(eventType)) {
            observers.get(eventType).remove(observer);
        }
    }

    public void notifyObservers(EventType eventType, Object arg) {
        if (observers.containsKey(eventType)) {
            for (Observer observer : observers.get(eventType)) {
                observer.update(arg);
            }
        }
    }
}
