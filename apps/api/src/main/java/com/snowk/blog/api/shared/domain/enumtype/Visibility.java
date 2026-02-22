package com.snowk.blog.api.shared.domain.enumtype;

import lombok.Getter;

@Getter
public enum Visibility {
    PUBLIC("PUBLIC", "공개"),
    PRIVATE("PRIVATE", "비공개");

    private final String code;
    private final String labelKo;

    Visibility(String code, String labelKo) {
        this.code = code;
        this.labelKo = labelKo;
    }
}
