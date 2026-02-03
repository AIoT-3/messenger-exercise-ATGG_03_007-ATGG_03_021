package com.message.controller;

import com.message.cofig.AppConfig;
import com.message.session.ClientSession;
import com.message.ui.*;
import com.message.ui.event.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@Slf4j
public class FrontController implements Runnable{
    private final static String DEFAULT_SERVER_ADDRESS = AppConfig.HOST;
    private final static int DEFAULT_PORT = AppConfig.PORT;

    private final String serverAddress;
    private final int serverPort;
    
    private final Subject subject;

    public FrontController() {
        this(DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);
    }

    public FrontController(String serverAddress, int serverPort){
        if(StringUtils.isEmpty(serverAddress) || serverPort <=0 ){
            throw new IllegalArgumentException("[FrontController] 서버 주소 혹은 서버 포트가 유효하지 않습니다.");
        }

        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.subject = new Subject();
    }

    @Override
    public void run() {
        try {
            ClientSession.connect();

            configSendObserver();
            
            // 패킷 리더 실행
            Thread packetReaderThread = new Thread(new PacketReader(subject));
            packetReaderThread.setDaemon(true);
            packetReaderThread.start();

            // ui 실행
            MessageClientForm.showUI(subject);

            while (!Thread.currentThread().isInterrupted()){
                Thread.sleep(1000);
            }

        }catch (Exception e){
            log.debug("message:{}",e.getMessage(),e);
            log.debug("client close");
        }finally {
            ClientSession.close();
        }
    }

    private void configSendObserver(){
        SendMessageAction sendMessageAction = new SendMessageAction();
        Observer observer = new MessageSendObserver(sendMessageAction);
        subject.register(EventType.SEND,observer);
    }
}