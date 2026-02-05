package com.message.thread.nio;

import com.message.handler.HandlerFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ThreadPoolFactory {
    private static final Map<String, ExecutorService> threadPools = new ConcurrentHashMap<>();

    static {
        List<String> methods = HandlerFactory.getMethods();
        for(String method: methods){
            threadPools.put(method, Executors.newFixedThreadPool(5));
        }
    }

    public static ExecutorService getThreadPool(String method){
        return threadPools.get(method);
    }
}
