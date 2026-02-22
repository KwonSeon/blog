package com.snowk.blog.api.project.infrastructure.persistence.adapter;

import static org.mockito.Mockito.verify;

import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.infrastructure.persistence.jpa.ProjectJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectRepositoryAdapterTest {

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @InjectMocks
    private ProjectRepositoryAdapter projectRepositoryAdapter;

    @Test
    @DisplayName("프로젝트 저장은 JPA 리포지토리에 위임된다")
    void save_delegatesToJpaRepository() {
        Project project = org.mockito.Mockito.mock(Project.class);

        projectRepositoryAdapter.save(project);

        verify(projectJpaRepository).save(project);
    }

    @Test
    @DisplayName("프로젝트 ID 조회는 JPA 리포지토리에 위임된다")
    void findById_delegatesToJpaRepository() {
        projectRepositoryAdapter.findById(1L);

        verify(projectJpaRepository).findById(1L);
    }

    @Test
    @DisplayName("프로젝트 슬러그 조회는 JPA 리포지토리에 위임된다")
    void findBySlug_delegatesToJpaRepository() {
        projectRepositoryAdapter.findBySlug("my-service");

        verify(projectJpaRepository).findBySlug("my-service");
    }
}
