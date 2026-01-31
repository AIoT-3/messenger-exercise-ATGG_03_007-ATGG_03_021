package com.message.domain;

import com.message.filter.FilterChain;
import com.message.filter.impl.SessionFilter;

public class FilterManagement {
    public static final FilterChain filterChain = new FilterChain();

    static {
        filterChain.addLastFilter(new SessionFilter());
    }
}
