package com.snowk.blog.api.project.infrastructure.persistence.jpa;

import com.snowk.blog.api.project.domain.entity.Project;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

    Optional<Project> findBySlug(String slug);
}
