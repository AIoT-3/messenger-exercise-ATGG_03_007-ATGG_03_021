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
        if (poolSize < 1 || Objects.isNull(runnable)) {
            throw new IllegalArgumentException();
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
