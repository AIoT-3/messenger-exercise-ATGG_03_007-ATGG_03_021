package com.message.mapper.dispatch;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ResponseMapperFactory {
    private static final Map<String, ResponseMapper> responseMapperMap = new HashMap<>();

    // TODO 수정사항
    static {
        // Reflections reflections = new Reflections("com.message.dispatch");
        Reflections reflections = new Reflections("com.message.mapper.dispatch"); // 이상한 곳 스캔하고 있었음. 경로 수정함

        Set<Class<? extends ResponseMapper>> classes = reflections.getSubTypesOf(ResponseMapper.class);

        for (Class<? extends ResponseMapper> clazz : classes) {
            try {
                ResponseMapper mapper = clazz.getDeclaredConstructor().newInstance();
                log.debug("messageHandler-factory init : instance : {}", mapper.getClass().getName());
                // 핸들러의 메서드 이름을 키로 하여 맵에 등록합니다.
                responseMapperMap.put(mapper.getClassName(), mapper);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public static ResponseMapper getResponseMapper(String className){
        return responseMapperMap.get(className);
    }
}
