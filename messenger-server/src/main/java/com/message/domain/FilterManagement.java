package com.message.domain;

import com.message.filter.Filter;
import com.message.filter.FilterChain;
import com.message.filter.impl.SessionFilter;

import java.util.ArrayList;
import java.util.List;

public class FilterManagement {
    // public static final FilterChain filterChain = new FilterChain();
    // 필터 목록만 static으로 관리
    private static final List<Filter> filters = new ArrayList<>();

    // TODO 수정사항 (재민)
    // filterChain이 static으로 공유되고 있는 문제 존재
    // race condition 발생 위험 있음
    // 필터 체인을 호출할 때마다 새로운 인스턴스를 생성하도록 하여 해결
    static {
        // filterChain.addLastFilter(new SessionFilter());
        filters.add(new SessionFilter());
        // 차후에 필터 추가 쉬워짐
    }

    // 호출할 때마다 새로운 체인을 생성해서 리턴 (Thread-safe)
    public static FilterChain getChain() {
        return new FilterChain(new ArrayList<>(filters)); // 원본 보호 위해 복사본 전달
    }
}
