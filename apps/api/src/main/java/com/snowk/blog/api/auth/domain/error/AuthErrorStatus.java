package com.snowk.blog.api.auth.domain.error;

import com.snowk.blog.api.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorStatus implements ErrorCode {
    ADMIN_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4011", "관리자 인증 정보가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    AuthErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
