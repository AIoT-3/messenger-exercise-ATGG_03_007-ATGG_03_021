package com.message.mapper.response;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ResponseMapperFactory {
    private static final Map<String, ResponseMapper> responseMapperMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("com.message.mapper");

        Set<Class<? extends AbstractResponseMapper>> classes = reflections.getSubTypesOf(AbstractResponseMapper.class);

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
