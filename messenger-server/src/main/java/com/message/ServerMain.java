package com.message;

import com.message.thread.runnable.MessageServer;
import lombok.extern.slf4j.Slf4j;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
@Slf4j
public class ServerMain {
    public static void main(String[] args) {
        MessageServer messageServer = new MessageServer();
        Thread thread = new Thread(messageServer);
        thread.start();
        log.info("스레드 시작합니다.");


        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}