package com.message.thread.channel;

import com.message.thread.executable.Executable;

import java.util.LinkedList;
import java.util.Queue;

public class RequestChannel {

    private final Queue<Executable> requestQueue;
    private long QUEUE_MAX_SIZE = 10;

    public RequestChannel() {
        this.requestQueue = new LinkedList<>();
    }

    public synchronized void addJob(Executable executable){
        while (requestQueue.size() >= QUEUE_MAX_SIZE){
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        requestQueue.add(executable);
        this.notifyAll();
    }

    public synchronized Executable getJob() {
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
