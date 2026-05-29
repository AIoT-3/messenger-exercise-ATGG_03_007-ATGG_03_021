package com.message.domain;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongIdManagement {

    private static final AtomicLongIdManagement INSTANCE = new AtomicLongIdManagement();

    private final AtomicLong roomIdSequence = new AtomicLong(1000);
    private final AtomicLong responseMessageIdSequence = new AtomicLong(1);
    private final AtomicLong chatMessageIdSequence = new AtomicLong(1);

    private AtomicLongIdManagement() {}

    public static AtomicLongIdManagement getInstance() {
        return INSTANCE;
    }

    public long getRoomIdSequenceIncrementAndGet() {
        return roomIdSequence.incrementAndGet();
    }

    public long getResponseMessageIdSequenceIncrementAndGet() {
        return responseMessageIdSequence.incrementAndGet();
    }

    public long getChatMessageIdSequenceIncrementAndGet() {
        return chatMessageIdSequence.incrementAndGet();
    }
}
