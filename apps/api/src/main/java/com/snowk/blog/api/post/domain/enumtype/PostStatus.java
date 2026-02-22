package com.snowk.blog.api.post.domain.enumtype;

import lombok.Getter;

@Getter
public enum PostStatus {
    DRAFT("DRAFT", "임시저장"),
    PUBLISHED("PUBLISHED", "발행");

    private final String code;
    private final String labelKo;

    PostStatus(String code, String labelKo) {
        this.code = code;
        this.labelKo = labelKo;
    }
}
