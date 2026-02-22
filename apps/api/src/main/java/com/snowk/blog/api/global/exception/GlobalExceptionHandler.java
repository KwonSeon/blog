package com.snowk.blog.api.global.exception;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.error.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 도메인/비즈니스 예외를 표준 에러 응답으로 변환
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        var errorCode = ex.getErrorCode();
        if (errorCode.getHttpStatus().is5xxServerError()) {
            log.error("[GlobalExceptionHandler] code={}, message={}", errorCode.getCode(), errorCode.getMessage());
        } else {
            log.info("[GlobalExceptionHandler] code={}, message={}", errorCode.getCode(), errorCode.getMessage());
        }
        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(ErrorResponse.of(errorCode));
    }

    // @Valid request body 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.info("[GlobalExceptionHandler] method argument not valid");
        return ResponseEntity.badRequest()
            .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST));
    }

    // @Validated의 파라미터/경로 변수 제약 조건 위반 처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.info("[GlobalExceptionHandler] constraint violation");
        return ResponseEntity.badRequest()
            .body(ErrorResponse.of(CommonErrorStatus.BAD_REQUEST));
    }

    // JSON 파싱 실패, 타입 불일치 등 request body 역직렬화 실패 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.info("[GlobalExceptionHandler] invalid request body");
        return ResponseEntity.badRequest()
            .body(ErrorResponse.of(CommonErrorStatus.INVALID_REQUEST_BODY));
    }

    // 미처리 예외의 최종 방어선: 내부 상세는 로그로만 남기고 500 표준 응답 반환
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("[GlobalExceptionHandler] unexpected error", ex);
        return ResponseEntity.status(CommonErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
            .body(ErrorResponse.of(CommonErrorStatus.INTERNAL_SERVER_ERROR));
    }
}
