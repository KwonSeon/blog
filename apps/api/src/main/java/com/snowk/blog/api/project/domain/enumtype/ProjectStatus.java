package com.snowk.blog.api.project.domain.enumtype;

import lombok.Getter;

@Getter
/**
 * 관리자 프로젝트의 운영 상태를 나타낸다.
 */
public enum ProjectStatus {
    /** 서비스가 현재 활성 상태인 프로젝트 */
    ACTIVE("ACTIVE", "활성"),

    /** 서비스 노출/운영을 중단한 비활성 프로젝트 */
    INACTIVE("INACTIVE", "비활성");

    private final String code;
    private final String labelKo;

    ProjectStatus(String code, String labelKo) {
        this.code = code;
        this.labelKo = labelKo;
    }
}
