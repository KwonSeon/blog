package com.snowk.blog.api.project.application.port.out;

import com.snowk.blog.api.project.domain.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryPort {

    Project save(Project project);

    void delete(Project project);

    boolean existsBySlug(String slug);

    // 관리자 상세 조회는 projectId 기준 단건 조회를 그대로 사용한다.
    Optional<Project> findById(Long projectId);

    // 관리자 목록 조회는 우선 전체 프로젝트 목록 반환으로 시작한다.
    List<Project> findAll();

    Optional<Project> findBySlug(String slug);
}
