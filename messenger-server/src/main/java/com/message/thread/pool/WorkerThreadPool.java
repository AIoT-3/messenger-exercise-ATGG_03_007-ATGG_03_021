package com.message.thread.pool;

import java.util.Objects;

public class WorkerThreadPool {
    private final int poolSize;
    private final static int DEFAULT_POOL_SIZE = 10;

    private final Thread[] threads;
    private final Runnable runnable;

    public WorkerThreadPool(Runnable runnable) {
        this(runnable, DEFAULT_POOL_SIZE);
    }

    public WorkerThreadPool(Runnable runnable, int poolSize) {
        if (poolSize < 1) {
            throw new IllegalArgumentException("스레드 풀 크기는 1 이상이어야 합니다. poolSize=" + poolSize);
        }
        if (Objects.isNull(runnable)) {
            throw new IllegalArgumentException("Runnable은 null일 수 없습니다.");
        }

        this.poolSize = poolSize;
        this.runnable = runnable;

        threads = new Thread[this.poolSize];
        init();
    }

    private void init() {
        for (int i = 0; i < poolSize; i++) {
            threads[i] = new Thread(runnable);
        }
    }

    public synchronized void start() {
        // threads에 초가화된 모든 Thread를 start 합니다
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public synchronized void stop() {
        for (Thread thread : threads) {
            thread.interrupt();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
