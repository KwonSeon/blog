package com.snowk.blog.api.project.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.support.ReflectionEntityFactory;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProjectJpaRepositoryTest {

    @Autowired
    private ProjectJpaRepository projectJpaRepository;

    @AfterEach
    void tearDown() {
        projectJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("슬러그로 프로젝트를 조회할 수 있다")
    void findBySlug_returnsSavedProject() {
        Project project = createProject("my-service", "My Service");

        projectJpaRepository.save(project);

        assertThat(projectJpaRepository.findBySlug("my-service")).isPresent();
    }

    @Test
    @DisplayName("동일한 슬러그의 프로젝트는 저장할 수 없다")
    void save_duplicateSlug_throwsDataIntegrityViolation() {
        projectJpaRepository.saveAndFlush(createProject("duplicate-project", "First"));

        assertThatThrownBy(() -> projectJpaRepository.saveAndFlush(createProject("duplicate-project", "Second")))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("제목이 없으면 프로젝트를 저장할 수 없다")
    void save_nullTitle_throwsDataIntegrityViolation() {
        Project project = createProject("null-title", "Temp");
        ReflectionEntityFactory.setField(project, "title", null);

        assertThatThrownBy(() -> projectJpaRepository.saveAndFlush(project))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("공개 목록 조회는 PUBLIC + ACTIVE 프로젝트만 반환한다")
    void findAllByVisibilityAndStatus_returnsOnlyPublicActiveProjects() {
        projectJpaRepository.saveAndFlush(createProject("public-active", "Public Active"));
        projectJpaRepository.saveAndFlush(createProject("private-active", "Private Active", Visibility.PRIVATE, ProjectStatus.ACTIVE));
        projectJpaRepository.saveAndFlush(createProject("public-inactive", "Public Inactive", Visibility.PUBLIC, ProjectStatus.INACTIVE));

        assertThat(projectJpaRepository.findAllByVisibilityAndStatus(Visibility.PUBLIC, ProjectStatus.ACTIVE))
            .extracting(Project::getSlug)
            .containsExactly("public-active");
    }

    @Test
    @DisplayName("공개 상세 조회는 slug와 공개 조건이 모두 일치할 때만 프로젝트를 반환한다")
    void findBySlugAndVisibilityAndStatus_returnsProject_whenPublicActive() {
        projectJpaRepository.saveAndFlush(createProject("public-detail", "Public Detail"));

        assertThat(
            projectJpaRepository.findBySlugAndVisibilityAndStatus(
                "public-detail",
                Visibility.PUBLIC,
                ProjectStatus.ACTIVE
            )
        ).isPresent();
    }

    private Project createProject(String slug, String title) {
        return createProject(
            slug,
            title,
            Visibility.PUBLIC,
            ProjectStatus.ACTIVE
        );
    }

    private Project createProject(String slug, String title, Visibility visibility, ProjectStatus status) {
        Project project = ReflectionEntityFactory.instantiate(Project.class);
        ReflectionEntityFactory.setField(project, "slug", slug);
        ReflectionEntityFactory.setField(project, "title", title);
        ReflectionEntityFactory.setField(project, "visibility", visibility);
        ReflectionEntityFactory.setField(project, "status", status);
        ReflectionEntityFactory.setField(project, "publishedAt", status == ProjectStatus.ACTIVE
            ? LocalDateTime.of(2026, 3, 8, 12, 0)
            : null);
        return project;
    }
}
