package com.message.filter;

import com.message.dto.HeaderDto;

public interface Filter {
    void doFilter(HeaderDto.RequestHeader header, FilterChain chain);
}
