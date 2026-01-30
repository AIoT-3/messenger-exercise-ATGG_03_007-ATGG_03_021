package com.message.thread.runnable;

import java.util.LinkedList;
import java.util.Queue;

public class RequestChannel {

    private final Queue<Runnable> requestQueue;
    private long QUEUE_MAX_SIZE = 10;

    public RequestChannel() {
        this.requestQueue = new LinkedList<>();
    }

    public synchronized void addJob(Runnable runnable){
        while (requestQueue.size() >= QUEUE_MAX_SIZE){
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        requestQueue.add(runnable);
        this.notifyAll();
    }

    public synchronized Runnable getJob() {
        while(requestQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }

        this.notifyAll();
        return requestQueue.poll();
    }

}
