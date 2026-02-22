package com.snowk.blog.api.project.infrastructure.persistence.adapter;

import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.domain.entity.Project;
import java.util.Optional;

import com.snowk.blog.api.project.infrastructure.persistence.jpa.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {

    private final ProjectJpaRepository projectJpaRepository;

    @Override
    public Project save(Project project) {
        return projectJpaRepository.save(project);
    }

    @Override
    public Optional<Project> findById(Long projectId) {
        return projectJpaRepository.findById(projectId);
    }

    @Override
    public Optional<Project> findBySlug(String slug) {
        return projectJpaRepository.findBySlug(slug);
    }
}
