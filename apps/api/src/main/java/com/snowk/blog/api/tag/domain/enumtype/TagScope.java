package com.snowk.blog.api.tag.domain.enumtype;

import lombok.Getter;

@Getter
public enum TagScope {
    POST("POST", "글"),
    PROJECT("PROJECT", "프로젝트");

    private final String code;
    private final String labelKo;

    TagScope(String code, String labelKo) {
        this.code = code;
        this.labelKo = labelKo;
    }
}
