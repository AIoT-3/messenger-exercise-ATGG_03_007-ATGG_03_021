package com.message;

import com.message.action.impl.ConsoleResponseAction;
import com.message.action.impl.RequestMessageAction;
import com.message.observer.Observer;
import com.message.observer.impl.MessageRecvObserver;
import com.message.observer.impl.MessageSendObserver;
import com.message.runnable.ReceivedMessageClient;
import com.message.session.ClientSession;
import com.message.subject.EventType;
import com.message.subject.MessageSubject;
import com.message.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConsoleClientMain {
    private static final Logger log = LoggerFactory.getLogger(ConsoleClientMain.class);

    public static void main(String[] args) {
        Subject subject = new MessageSubject();
        Thread receiverThread = null;

        try {
            // 1. 서버 연결 (Managed by ClientSession)
            ClientSession.connect();

            // 2. Observer 설정 (SEND)
            RequestMessageAction sendAction = new RequestMessageAction();
            Observer sendObserver = new MessageSendObserver(sendAction);
            subject.register(EventType.SEND, sendObserver);
            log.debug("SEND Observer registered.");

            // 3. Observer 설정 (RECV)
            ConsoleResponseAction recvAction = new ConsoleResponseAction();
            Observer recvObserver = new MessageRecvObserver(recvAction);
            subject.register(EventType.RECV, recvObserver);
            log.debug("RECV Observer registered.");

            // 4. 수신 스레드 시작
            Socket socket = ClientSession.getSocket();
            ReceivedMessageClient receiver = new ReceivedMessageClient(socket, subject);
            receiverThread = new Thread(receiver, "ConsoleReceivedMessageClient");
            receiverThread.setDaemon(true);
            receiverThread.start();
            log.info("Message receiver thread started.");

            // 5. 콘솔 입력 처리
            System.out.println("==================================================");
            System.out.println("Console Messenger Client");
            System.out.println("Enter commands starting with a forward slash '/'.");
            System.out.println("Example: /LOGIN user password");
            System.out.println("Type /exit to quit.");
            System.out.println("==================================================");

            runInputLoop(subject);

        } catch (Exception e) {
            log.error("Client failed to start or run.", e);
            System.err.println("Client encountered a fatal error: " + e.getMessage());
        } finally {
            log.info("Client is shutting down...");
            // 자원 정리
            if (receiverThread != null && receiverThread.isAlive()) {
                receiverThread.interrupt(); // Signal the receiver thread to stop
            }
            ClientSession.close();
            log.info("Client has been shut down.");
        }
    }

    private static void runInputLoop(Subject subject) {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = consoleReader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.equalsIgnoreCase("/exit")) {
                    // Send a logout message if authenticated, then break
                    if (ClientSession.isAuthenticated()) {
                        subject.sendMessage("logout");
                    }
                    break;
                }

                if (trimmedLine.isEmpty() || !trimmedLine.startsWith("/")) {
                    System.out.println("Invalid command. Please start your command with a '/'.");
                    continue;
                }

                // Pass the raw command string (without '/') to the subject
                // Example: "login myuser mypassword"
                String commandString = trimmedLine.substring(1);
                subject.sendMessage(commandString);
            }
        } catch (Exception e) {
            log.error("Error during console input.", e);
            System.err.println("An error occurred while reading input.");
        }
    }
}