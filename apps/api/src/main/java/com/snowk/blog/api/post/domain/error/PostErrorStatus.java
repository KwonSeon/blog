package com.snowk.blog.api.post.domain.error;

import com.snowk.blog.api.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PostErrorStatus implements ErrorCode {
    INVALID_POST_SLUG(HttpStatus.BAD_REQUEST, "POST4001", "게시글 slug가 올바르지 않습니다."),
    INVALID_POST_TITLE(HttpStatus.BAD_REQUEST, "POST4002", "게시글 title이 올바르지 않습니다."),
    INVALID_POST_CONTENT(HttpStatus.BAD_REQUEST, "POST4003", "게시글 content가 올바르지 않습니다."),
    INVALID_POST_VISIBILITY(HttpStatus.BAD_REQUEST, "POST4004", "게시글 visibility가 올바르지 않습니다."),
    INVALID_POST_STATUS(HttpStatus.BAD_REQUEST, "POST4005", "게시글 status가 올바르지 않습니다."),
    INVALID_POST_LANGUAGE(HttpStatus.BAD_REQUEST, "POST4006", "게시글 lang이 올바르지 않습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4041", "요청한 게시글을 찾을 수 없습니다."),
    DUPLICATE_POST_SLUG(HttpStatus.CONFLICT, "POST4091", "동일한 slug를 가진 게시글이 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    PostErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
