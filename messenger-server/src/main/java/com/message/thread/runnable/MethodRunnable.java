package com.message.thread.runnable;

import com.message.handler.Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MethodRunnable implements Runnable {
    private final Handler handler;

    @Override
    public void run() {
        // TODO body를 받는 걸 구현을 해야됨
        String value = "";
        handler.execute(value);
    }
}
