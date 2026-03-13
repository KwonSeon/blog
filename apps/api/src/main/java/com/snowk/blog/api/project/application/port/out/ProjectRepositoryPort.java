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

    // 공개 목록 조회는 공개 노출 조건을 만족하는 프로젝트만 반환한다.
    List<Project> findPublicProjects();

    // 공개 상세 조회는 slug와 공개 노출 조건을 기준으로 단건 조회한다.
    Optional<Project> findPublicProjectBySlug(String slug);

    Optional<Project> findBySlug(String slug);
}
