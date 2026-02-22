package com.snowk.blog.api.project.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.support.ReflectionEntityFactory;
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

    private Project createProject(String slug, String title) {
        Project project = ReflectionEntityFactory.instantiate(Project.class);
        ReflectionEntityFactory.setField(project, "slug", slug);
        ReflectionEntityFactory.setField(project, "title", title);
        return project;
    }
}
