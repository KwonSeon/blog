package com.snowk.blog.api.project.infrastructure.persistence.adapter;

import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.infrastructure.persistence.jpa.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 현재 프로젝트 CRUD 단계에서는 저장/조회/삭제 규칙을 adapter에서 가공하지 않고,
 * `ProjectRepositoryPort` 요청을 JPA repository에 그대로 위임한다.
 */
@Repository
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {

    private final ProjectJpaRepository projectJpaRepository;

    @Override
    public Project save(Project project) {
        return projectJpaRepository.save(project);
    }

    @Override
    public void delete(Project project) {
        projectJpaRepository.delete(project);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return projectJpaRepository.existsBySlug(slug);
    }

    @Override
    public Optional<Project> findById(Long projectId) {
        return projectJpaRepository.findById(projectId);
    }

    @Override
    public List<Project> findAll() {
        return projectJpaRepository.findAll();
    }

    @Override
    public Optional<Project> findBySlug(String slug) {
        return projectJpaRepository.findBySlug(slug);
    }
}
