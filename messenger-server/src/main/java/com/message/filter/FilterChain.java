package com.message.filter;

import com.message.dto.RequestDto;
import com.message.filter.impl.LoginStateCheckFilter;
import com.message.filter.impl.SessionFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class FilterChain {
    private final List<Filter> filters;
    private Iterator<Filter> filterIterator;


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
    
    private void iterator(){
        filterIterator = filters.iterator();
    }

    /**
     * 필터 체인의 다음 필터를 실행합니다.
     *
     * @param request 헤더
     */
    public void doFilter(RequestDto request) {
        if(filterIterator.hasNext()){
            Filter next = filterIterator.next();
            next.doFilter(request, this);
            log.debug("[Filter Chain] filterName: {}", next.getClass().getName());
        }
    }

    public static FilterChain getFilterChain() {
        FilterChain filterChain = new FilterChain()
                .addLastFilter(new SessionFilter())
                .addLastFilter(new LoginStateCheckFilter());
        filterChain.iterator();
        return filterChain;
    }
}
