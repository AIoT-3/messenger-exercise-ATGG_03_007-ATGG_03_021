package com.message.filter;

import com.message.dto.RequestDto;

public interface Filter {
    void doFilter(RequestDto request, FilterChain chain);
}
