package com.snowk.blog.api.common.domain.enumtype;

import lombok.Getter;

@Getter
/**
 * 콘텐츠의 공개 여부를 나타낸다.
 */
public enum Visibility {
    /** 외부 사용자에게 공개되는 상태 */
    PUBLIC("PUBLIC", "공개"),

    /** 관리자만 볼 수 있는 비공개 상태 */
    PRIVATE("PRIVATE", "비공개");

    private final String code;
    private final String labelKo;

    Visibility(String code, String labelKo) {
        this.code = code;
        this.labelKo = labelKo;
    }
}
