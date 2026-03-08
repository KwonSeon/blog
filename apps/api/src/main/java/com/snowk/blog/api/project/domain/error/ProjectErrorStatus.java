package com.snowk.blog.api.project.domain.error;

import com.snowk.blog.api.global.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
/**
 * 프로젝트 도메인 검증 실패 시 사용하는 에러 코드다.
 */
public enum ProjectErrorStatus implements ErrorCode {
    /** slug 값이 비어 있거나 허용되지 않는 상태 */
    INVALID_PROJECT_SLUG(HttpStatus.BAD_REQUEST, "PROJECT4001", "프로젝트 slug가 올바르지 않습니다."),

    /** title 값이 비어 있거나 허용되지 않는 상태 */
    INVALID_PROJECT_TITLE(HttpStatus.BAD_REQUEST, "PROJECT4002", "프로젝트 title이 올바르지 않습니다."),

    /** visibility 값이 누락되었거나 허용되지 않는 상태 */
    INVALID_PROJECT_VISIBILITY(HttpStatus.BAD_REQUEST, "PROJECT4003", "프로젝트 visibility가 올바르지 않습니다."),

    /** status 값이 누락되었거나 허용되지 않는 상태 */
    INVALID_PROJECT_STATUS(HttpStatus.BAD_REQUEST, "PROJECT4004", "프로젝트 status가 올바르지 않습니다."),

    /** 요청한 프로젝트를 찾을 수 없는 상태 */
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT4041", "요청한 프로젝트를 찾을 수 없습니다."),

    /** 동일한 slug를 가진 프로젝트가 이미 존재하는 상태 */
    DUPLICATE_PROJECT_SLUG(HttpStatus.CONFLICT, "PROJECT4091", "동일한 slug를 가진 프로젝트가 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ProjectErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
