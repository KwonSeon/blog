package com.snowk.blog.api.user.domain.enumtype;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN", "관리자"),
    USER("USER", "일반 사용자");

    private final String code;
    private final String labelKo;

    UserRole(String code, String labelKo) {
        this.code = code;
        this.labelKo = labelKo;
    }
}
