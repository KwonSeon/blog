package com.snowk.blog.api.project.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.command.CreateProjectCommand;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.result.CreateProjectResult;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateProjectServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @InjectMocks
    private CreateProjectService createProjectService;

    @Test
    @DisplayName("프로젝트 생성 성공 시 저장 결과를 응답으로 반환한다")
    void createProject_returnsSavedProject_whenCommandIsValid() {
        CreateProjectCommand command = new CreateProjectCommand(
            "snowk-blog",
            "Snowk Blog",
            "개인 블로그 프로젝트",
            "https://blog.s-nowk.com",
            "https://github.com/s-nowk/blog",
            Visibility.PUBLIC,
            ProjectStatus.ACTIVE,
            10L
        );
        Project savedProject = mockSavedProject();
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);

        when(projectRepositoryPort.existsBySlug("snowk-blog")).thenReturn(false);
        when(projectRepositoryPort.save(projectCaptor.capture())).thenReturn(savedProject);

        CreateProjectResult result = createProjectService.createProject(command);

        Project createdProject = projectCaptor.getValue();
        assertThat(createdProject.getSlug()).isEqualTo("snowk-blog");
        assertThat(createdProject.getTitle()).isEqualTo("Snowk Blog");
        assertThat(createdProject.getSummary()).isEqualTo("개인 블로그 프로젝트");
        assertThat(createdProject.getServiceUrl()).isEqualTo("https://blog.s-nowk.com");
        assertThat(createdProject.getRepoUrl()).isEqualTo("https://github.com/s-nowk/blog");
        assertThat(createdProject.getVisibility()).isEqualTo(Visibility.PUBLIC);
        assertThat(createdProject.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(createdProject.getCoverMediaAssetId()).isEqualTo(10L);
        assertThat(createdProject.getPublishedAt()).isNotNull();

        assertThat(result.projectId()).isEqualTo(1L);
        assertThat(result.slug()).isEqualTo("snowk-blog");
        assertThat(result.title()).isEqualTo("Snowk Blog");
        assertThat(result.summary()).isEqualTo("개인 블로그 프로젝트");
        assertThat(result.serviceUrl()).isEqualTo("https://blog.s-nowk.com");
        assertThat(result.repoUrl()).isEqualTo("https://github.com/s-nowk/blog");
        assertThat(result.visibility()).isEqualTo(Visibility.PUBLIC);
        assertThat(result.status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(result.coverMediaAssetId()).isEqualTo(10L);
        assertThat(result.publishedAt()).isEqualTo(LocalDateTime.of(2026, 3, 8, 12, 0));
        assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2026, 3, 8, 12, 1));
        assertThat(result.updatedAt()).isEqualTo(LocalDateTime.of(2026, 3, 8, 12, 2));

        verify(projectRepositoryPort).existsBySlug("snowk-blog");
        verify(projectRepositoryPort).save(createdProject);
    }

    @Test
    @DisplayName("slug가 중복되면 409 예외를 반환하고 저장하지 않는다")
    void createProject_throwsException_whenSlugAlreadyExists() {
        CreateProjectCommand command = new CreateProjectCommand(
            "snowk-blog",
            "Snowk Blog",
            "개인 블로그 프로젝트",
            "https://blog.s-nowk.com",
            "https://github.com/s-nowk/blog",
            Visibility.PUBLIC,
            ProjectStatus.ACTIVE,
            10L
        );

        when(projectRepositoryPort.existsBySlug("snowk-blog")).thenReturn(true);

        BaseException exception = catchThrowableOfType(
            () -> createProjectService.createProject(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ProjectErrorStatus.DUPLICATE_PROJECT_SLUG);
        verify(projectRepositoryPort).existsBySlug("snowk-blog");
        verify(projectRepositoryPort, never()).save(org.mockito.ArgumentMatchers.any(Project.class));
    }

    @Test
    @DisplayName("필수값이 비어 있으면 도메인 검증 예외를 반환한다")
    void createProject_throwsException_whenCommandHasInvalidRequiredField() {
        CreateProjectCommand command = new CreateProjectCommand(
            "snowk-blog",
            " ",
            "개인 블로그 프로젝트",
            "https://blog.s-nowk.com",
            "https://github.com/s-nowk/blog",
            Visibility.PUBLIC,
            ProjectStatus.ACTIVE,
            10L
        );

        when(projectRepositoryPort.existsBySlug("snowk-blog")).thenReturn(false);

        BaseException exception = catchThrowableOfType(
            () -> createProjectService.createProject(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ProjectErrorStatus.INVALID_PROJECT_TITLE);
        verify(projectRepositoryPort).existsBySlug("snowk-blog");
        verify(projectRepositoryPort, never()).save(org.mockito.ArgumentMatchers.any(Project.class));
    }

    private Project mockSavedProject() {
        Project project = org.mockito.Mockito.mock(Project.class);
        when(project.getProjectId()).thenReturn(1L);
        when(project.getSlug()).thenReturn("snowk-blog");
        when(project.getTitle()).thenReturn("Snowk Blog");
        when(project.getSummary()).thenReturn("개인 블로그 프로젝트");
        when(project.getServiceUrl()).thenReturn("https://blog.s-nowk.com");
        when(project.getRepoUrl()).thenReturn("https://github.com/s-nowk/blog");
        when(project.getVisibility()).thenReturn(Visibility.PUBLIC);
        when(project.getStatus()).thenReturn(ProjectStatus.ACTIVE);
        when(project.getCoverMediaAssetId()).thenReturn(10L);
        when(project.getPublishedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 12, 0));
        when(project.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 12, 1));
        when(project.getUpdatedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 12, 2));
        return project;
    }
}
