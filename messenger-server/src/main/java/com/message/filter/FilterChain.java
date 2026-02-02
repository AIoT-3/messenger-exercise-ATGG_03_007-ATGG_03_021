package com.message.filter;

import com.message.dto.RequestDto;
import com.message.filter.impl.LoginStateCheckFilter;
import com.message.filter.impl.SessionFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class FilterChain {
    private final List<Filter> filters;
    private int index = 0;

    public FilterChain() {
        filters = new LinkedList<>();
    }

    // 주입받는 생성자 추가
    public FilterChain(List<Filter> filters) {
        this.filters = filters;
    }

    /**
     * 필터 체인에 필터를 추가합니다.
     *
     * @param filter 추가할 필터
     */
    public FilterChain addLastFilter(Filter filter) {
        filters.addLast(filter);
        return this;
    }

    /**
     * 필터 체인의 다음 필터를 실행합니다.
     *
     * @param request 헤더
     */
    public void doFilter(RequestDto request) {
        log.debug("[Filter Chain] ChainSize: {}", filters.size());
        if (index < filters.size()) {
            Filter filter = filters.get(index);
            log.debug("[Filter Chain] index: {}, filterName: {}", index++, filter.getClass().getName());
            filter.doFilter(request, this);
        }
    }

    public void reset(){
        index = 0;
    }

    public static FilterChain getFilterChain() {
        return new FilterChain()
                .addLastFilter(new SessionFilter())
                .addLastFilter(new LoginStateCheckFilter());
    }
}
