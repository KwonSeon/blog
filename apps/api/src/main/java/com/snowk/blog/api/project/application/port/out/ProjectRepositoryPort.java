package com.snowk.blog.api.project.application.port.out;

import com.snowk.blog.api.project.domain.entity.Project;

import java.util.Optional;

public interface ProjectRepositoryPort {

    Project save(Project project);

    Optional<Project> findById(Long projectId);

    Optional<Project> findBySlug(String slug);
}
