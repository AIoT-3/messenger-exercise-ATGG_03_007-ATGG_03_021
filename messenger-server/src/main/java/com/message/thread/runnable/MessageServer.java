package com.message.thread.runnable;

import com.message.config.AppConfig;
import com.message.thread.executable.MessageDispatcher;
import com.message.thread.pool.WorkerThreadPool;
import com.message.thread.channel.RequestChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class MessageServer implements Runnable {

    private final int port;
    private final ServerSocket serverSocket;
    private final WorkerThreadPool workerThreadPool;
    private final RequestChannel requestChannel;

    private static final Map<String, Socket> clientMap = new ConcurrentHashMap<>();

    public MessageServer() {
        this(AppConfig.PORT);
    }

    public MessageServer(int port) {
        if (port <= 0) {
            throw new IllegalArgumentException(String.format("port:%d", port));
        }

        this.port = port;

        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        requestChannel = new RequestChannel();
        workerThreadPool = new WorkerThreadPool(() -> requestChannel.getJob().execute());
    }

    @Override
    public void run() {
        //thread pool start
        workerThreadPool.start();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket client = serverSocket.accept();
                requestChannel.addJob(new MessageDispatcher(client));
            } catch (IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static boolean addClient(String id, Socket socket) {
        if (clientMap.containsKey(id)) {
            log.debug("id: {}, aready exist client socket!", id);
            return false;
        }

        clientMap.put(id, socket);
        return true;
    }

    public static List<String> getClientIds() {
        return clientMap.keySet().stream().collect(Collectors.toList());
    }

    public static Socket getClientSocket(String id) {
        return clientMap.get(id);
    }

    public static void removeClient(String id) {
        if (!StringUtils.isEmpty(id)) {
            clientMap.remove(id);
        }
    }
}
