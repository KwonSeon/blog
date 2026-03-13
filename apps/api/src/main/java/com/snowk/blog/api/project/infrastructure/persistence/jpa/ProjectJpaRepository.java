package com.snowk.blog.api.project.infrastructure.persistence.jpa;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

    // save/findById/findAll/delete는 JpaRepository 기본 메서드를 그대로 사용한다.
    boolean existsBySlug(String slug);

    List<Project> findAllByVisibilityAndStatus(Visibility visibility, ProjectStatus status);

    Optional<Project> findBySlugAndVisibilityAndStatus(String slug, Visibility visibility, ProjectStatus status);

    Optional<Project> findBySlug(String slug);
}
