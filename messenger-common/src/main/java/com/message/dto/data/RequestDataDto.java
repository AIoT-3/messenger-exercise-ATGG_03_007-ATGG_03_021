package com.message.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface RequestDataDto {
    @JsonIgnore
    String getType();
}
