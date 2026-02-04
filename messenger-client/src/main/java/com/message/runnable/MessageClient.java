package com.message.runnable;

import com.message.action.impl.RequestMessageAction;
import com.message.action.impl.ResponseMessageAction;
import com.message.cofig.AppConfig;
import com.message.observer.Observer;
import com.message.observer.impl.MessageRecvObserver;
import com.message.observer.impl.MessageSendObserver;
import com.message.session.ClientSession;
import com.message.subject.EventType;
import com.message.subject.MessageSubject;
import com.message.subject.Subject;
import com.message.ui.form.MessageClientForm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.net.Socket;

/**
 * 메신저 클라이언트 메인 클래스
 * 서버 연결, UI 실행, 메시지 송수신 관리
 */
public class MessageClient implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MessageClient.class);

    private static final String DEFAULT_SERVER_ADDRESS = AppConfig.HOST;
    private static final int DEFAULT_PORT = AppConfig.PORT;

    private final String serverAddress;
    private final int serverPort;
    private final Subject subject;

    private MessageClientForm form;
    private Thread receiverThread;

    public MessageClient() {
        this(DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);
    }

    public MessageClient(String serverAddress, int serverPort) {
        if (StringUtils.isEmpty(serverAddress) || serverPort <= 0) {
            throw new IllegalArgumentException("[MessageClient] 서버 주소 혹은 서버 포트가 유효하지 않습니다.");
        }

        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.subject = new MessageSubject();
    }

    @Override
    public void run() {
        try {
            // 1. 서버 연결
            ClientSession.connect();
            log.info("서버에 연결되었습니다 - {}:{}", serverAddress, serverPort);

            // 2. UI 실행 (Swing EDT에서 실행)
            SwingUtilities.invokeAndWait(() -> {
                form = MessageClientForm.showUI(subject);
            });

            // 3. Observer 설정
            configureObservers();

            // 4. 수신 스레드 시작
            startReceiverThread();

            // 5. 메인 스레드는 종료 대기
            while (!Thread.currentThread().isInterrupted() && form.isDisplayable()) {
                Thread.sleep(100);
            }

        } catch (Exception e) {
            log.error("클라이언트 실행 중 오류 발생: {}", e.getMessage(), e);
            showErrorAndExit("서버 연결에 실패했습니다: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Observer들을 설정
     */
    private void configureObservers() {
        // SEND Observer - 요청 메시지를 서버로 전송
        RequestMessageAction sendAction = new RequestMessageAction();
        Observer sendObserver = new MessageSendObserver(sendAction);
        subject.register(EventType.SEND, sendObserver);
        log.debug("SEND Observer 등록 완료");

        // RECV Observer - 응답 메시지를 UI에 반영
        ResponseMessageAction recvAction = new ResponseMessageAction(form);
        Observer recvObserver = new MessageRecvObserver(recvAction);
        subject.register(EventType.RECV, recvObserver);
        log.debug("RECV Observer 등록 완료");
    }

    /**
     * 메시지 수신 스레드 시작
     */
    private void startReceiverThread() {
        Socket socket = ClientSession.getSocket();
        ReceivedMessageClient receiver = new ReceivedMessageClient(socket, subject);
        receiverThread = new Thread(receiver, "ReceivedMessageClient");
        receiverThread.setDaemon(true);
        receiverThread.start();
        log.info("메시지 수신 스레드 시작됨");
    }

    /**
     * 에러 메시지 표시 후 종료
     */
    private void showErrorAndExit(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null,
                message,
                "연결 오류",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }

    /**
     * 리소스 정리
     */
    private void cleanup() {
        log.info("클라이언트 종료 중...");

        // 수신 스레드 종료
        if (receiverThread != null && receiverThread.isAlive()) {
            receiverThread.interrupt();
        }

        // 세션 연결 종료
        ClientSession.close();

        log.info("클라이언트가 종료되었습니다.");
    }

    /**
     * Subject 반환 (테스트용)
     */
    public Subject getSubject() {
        return subject;
    }
}