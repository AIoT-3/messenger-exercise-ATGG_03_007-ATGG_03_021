package com.message.ui;

import com.message.dto.ResponseDto;
import com.message.session.ClientSession;
import com.message.ui.event.EventType;
import com.message.ui.event.Subject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PacketReader implements Runnable {
    private final Subject subject;

    public PacketReader(Subject subject) {
        this.subject = subject;
    }

    @Override
    public void run() {
        log.info("PacketReader started");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ResponseDto response = ClientSession.receive();
                subject.notifyObservers(EventType.RECV, response);
            } catch (Exception e) {
                log.error("Error reading packet", e);
                subject.notifyObservers(EventType.RECV, "Connection lost: " + e.getMessage());
                break;
            }
        }
    }
}
