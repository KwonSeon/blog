package com.snowk.blog.api.tag.domain.enumtype;

import lombok.Getter;

@Getter
public enum TagKind {
    CATEGORY("CATEGORY", "카테고리"),
    TAG("TAG", "태그");

    private final String code;
    private final String labelKo;

    TagKind(String code, String labelKo) {
        this.code = code;
        this.labelKo = labelKo;
    }
}
