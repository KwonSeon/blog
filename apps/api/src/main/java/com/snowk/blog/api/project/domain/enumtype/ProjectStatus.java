package com.snowk.blog.api.project.domain.enumtype;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    ACTIVE("ACTIVE", "활성"),
    INACTIVE("INACTIVE", "비활성");

    private final String code;
    private final String labelKo;

    ProjectStatus(String code, String labelKo) {
        this.code = code;
        this.labelKo = labelKo;
    }
}
