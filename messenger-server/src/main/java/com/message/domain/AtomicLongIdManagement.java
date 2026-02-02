package com.message.domain;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongIdManagement {
    private static final AtomicLong roomIdSequence = new AtomicLong(1000);
    private static final AtomicLong messageIdSequence = new AtomicLong(1);

    public static long getRoomIdSequenceIncreateAndGet(){
        return roomIdSequence.incrementAndGet();
    }

    public static long getMessageIdSequenceIncreateAndGet(){
        return messageIdSequence.incrementAndGet();
    }
}
