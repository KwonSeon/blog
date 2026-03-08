package com.snowk.blog.api.project.infrastructure.persistence.jpa;

import com.snowk.blog.api.project.domain.entity.Project;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

    // save/findById/findAll/delete는 JpaRepository 기본 메서드를 그대로 사용한다.
    boolean existsBySlug(String slug);

    Optional<Project> findBySlug(String slug);
}
