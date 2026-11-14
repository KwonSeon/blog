package com.snowk.blog.api.project.infrastructure.persistence.jpa;
import com.snowk.blog.api.project.domain.entity.ProjectStory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProjectStoryRepository extends JpaRepository<ProjectStory,Long> {}
