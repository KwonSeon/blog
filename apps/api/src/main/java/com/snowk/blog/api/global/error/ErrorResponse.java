package com.snowk.blog.api.global.error;

import java.time.LocalDateTime;

public record ErrorResponse(
    boolean isSuccess,
    String code,
    String message,
    int status,
    LocalDateTime timestamp
) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
            false,
            errorCode.getCode(),
            errorCode.getMessage(),
            errorCode.getHttpStatus().value(),
            LocalDateTime.now()
        );
    }
}
