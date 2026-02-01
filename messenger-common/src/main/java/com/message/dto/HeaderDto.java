package com.message.dto;

import java.time.OffsetDateTime;

public class HeaderDto {
    public record RequestHeader(
            String type,
            OffsetDateTime timestamp,
            String sessionId
    ){}

    public record ResponseHeader(
            String type,
            boolean success,
            OffsetDateTime timestamp,
            // TODO 수정사항 (재민)
            // 명세서: 서버는 메시지를 수신하면 고유한 long 타입의 messageId를 생성하여 응답에 포함합니다.
            // 클라이언트는 이를 통해 메시지 확인이나 추적을 할 수 있습니다.
            long messageId // 따라서 추가함
    ){}
}
