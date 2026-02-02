package com.message.filter;

import com.message.dto.HeaderDto;

import java.util.LinkedList;
import java.util.List;

public class FilterChain {
    private List<Filter> filters = new LinkedList<>();
    private int index = 0;

    // TODO 수정사항 (재민)
    // 주입받는 생성자 추가
    public FilterChain(List<Filter> filters) {
        this.filters = filters;
    }

    /**
     * 필터 체인에 필터를 추가합니다.
     *
     * @param filter 추가할 필터
     */
    public void addLastFilter(Filter filter) {
        filters.addLast(filter);
    }

    /**
     * 필터 체인의 다음 필터를 실행합니다.
     *
     * @param header 요청 헤더
     */
    public void doFilter(HeaderDto.RequestHeader header) {
        if (index < filters.size()) {
            Filter filter = filters.get(index++);
            filter.doFilter(header, this);
        }
    }
}
