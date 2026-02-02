package com.message.config;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {
    
    //세션이 유효한지 체크 (비 로그인상태인지 확인)
    public static List<String> SessionCheckMethodName = new ArrayList<>();

    static {
        SessionCheckMethodName.add("LOGIN");
    }

}