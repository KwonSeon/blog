package com.snowk.blog.api.project.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.command.UpdateProjectCommand;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.result.UpdateProjectResult;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.support.ReflectionEntityFactory;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateProjectServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @InjectMocks
    private UpdateProjectService updateProjectService;

    @Test
    @DisplayName("프로젝트 수정 성공 시 변경된 상태를 결과로 반환한다")
    void updateProject_returnsUpdatedResult_whenCommandIsValid() {
        UpdateProjectCommand command = new UpdateProjectCommand(
            1L,
            "snowk-blog",
            "Snowk Blog V2",
            "수정된 프로젝트 요약",
            "https://blog-v2.s-nowk.com",
            "https://github.com/s-nowk/blog-v2",
            Visibility.PRIVATE,
            ProjectStatus.ACTIVE,
            20L
        );
        Project project = createProject(
            1L,
            "snowk-blog",
            "Snowk Blog",
            "기존 프로젝트 요약",
            "https://blog.s-nowk.com",
            "https://github.com/s-nowk/blog",
            Visibility.PUBLIC,
            ProjectStatus.ACTIVE,
            10L,
            LocalDateTime.of(2026, 3, 8, 12, 0),
            LocalDateTime.of(2026, 3, 8, 12, 1),
            LocalDateTime.of(2026, 3, 8, 12, 2)
        );

        when(projectRepositoryPort.findById(1L)).thenReturn(Optional.of(project));

        UpdateProjectResult result = updateProjectService.updateProject(command);

        assertThat(project.getSlug()).isEqualTo("snowk-blog");
        assertThat(project.getTitle()).isEqualTo("Snowk Blog V2");
        assertThat(project.getSummary()).isEqualTo("수정된 프로젝트 요약");
        assertThat(project.getServiceUrl()).isEqualTo("https://blog-v2.s-nowk.com");
        assertThat(project.getRepoUrl()).isEqualTo("https://github.com/s-nowk/blog-v2");
        assertThat(project.getVisibility()).isEqualTo(Visibility.PRIVATE);
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(project.getCoverMediaAssetId()).isEqualTo(20L);
        assertThat(project.getPublishedAt()).isEqualTo(LocalDateTime.of(2026, 3, 8, 12, 0));

        assertThat(result.projectId()).isEqualTo(1L);
        assertThat(result.slug()).isEqualTo("snowk-blog");
        assertThat(result.title()).isEqualTo("Snowk Blog V2");
        assertThat(result.summary()).isEqualTo("수정된 프로젝트 요약");
        assertThat(result.serviceUrl()).isEqualTo("https://blog-v2.s-nowk.com");
        assertThat(result.repoUrl()).isEqualTo("https://github.com/s-nowk/blog-v2");
        assertThat(result.visibility()).isEqualTo(Visibility.PRIVATE);
        assertThat(result.status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(result.coverMediaAssetId()).isEqualTo(20L);
        assertThat(result.publishedAt()).isEqualTo(project.getPublishedAt());
        assertThat(result.createdAt()).isEqualTo(project.getCreatedAt());
        assertThat(result.updatedAt()).isEqualTo(project.getUpdatedAt());

        verify(projectRepositoryPort).findById(1L);
        verify(projectRepositoryPort, never()).findBySlug(anyString());
        verify(projectRepositoryPort, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("비활성 프로젝트를 활성 상태로 수정하면 publishedAt을 채운다")
    void updateProject_setsPublishedAt_whenActivatingInactiveProject() {
        UpdateProjectCommand command = new UpdateProjectCommand(
            1L,
            "snowk-blog",
            "Snowk Blog",
            "다시 활성화된 프로젝트",
            "https://blog.s-nowk.com",
            "https://github.com/s-nowk/blog",
            Visibility.PUBLIC,
            ProjectStatus.ACTIVE,
            10L
        );
        Project project = createProject(
            1L,
            "snowk-blog",
            "Snowk Blog",
            "비활성 프로젝트",
            "https://blog.s-nowk.com",
            "https://github.com/s-nowk/blog",
            Visibility.PUBLIC,
            ProjectStatus.INACTIVE,
            10L,
            null,
            LocalDateTime.of(2026, 3, 8, 12, 1),
            LocalDateTime.of(2026, 3, 8, 12, 2)
        );

        when(projectRepositoryPort.findById(1L)).thenReturn(Optional.of(project));

        UpdateProjectResult result = updateProjectService.updateProject(command);

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(project.getPublishedAt()).isNotNull();
        assertThat(result.publishedAt()).isEqualTo(project.getPublishedAt());

        verify(projectRepositoryPort).findById(1L);
        verify(projectRepositoryPort, never()).findBySlug(anyString());
        verify(projectRepositoryPort, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("수정 대상 프로젝트가 없으면 404 예외를 반환한다")
    void updateProject_throwsException_whenProjectDoesNotExist() {
        UpdateProjectCommand command = new UpdateProjectCommand(
            1L,
            "snowk-blog",
            "Snowk Blog V2",
            "수정된 프로젝트 요약",
            "https://blog-v2.s-nowk.com",
            "https://github.com/s-nowk/blog-v2",
            Visibility.PRIVATE,
            ProjectStatus.ACTIVE,
            20L
        );

        when(projectRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        BaseException exception = catchThrowableOfType(
            () -> updateProjectService.updateProject(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ProjectErrorStatus.PROJECT_NOT_FOUND);

        verify(projectRepositoryPort).findById(1L);
        verify(projectRepositoryPort, never()).findBySlug(anyString());
        verify(projectRepositoryPort, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("다른 프로젝트가 같은 slug를 사용 중이면 409 예외를 반환한다")
    void updateProject_throwsException_whenSlugBelongsToAnotherProject() {
        UpdateProjectCommand command = new UpdateProjectCommand(
            1L,
            "snowk-blog-v2",
            "Snowk Blog V2",
            "수정된 프로젝트 요약",
            "https://blog-v2.s-nowk.com",
            "https://github.com/s-nowk/blog-v2",
            Visibility.PRIVATE,
            ProjectStatus.ACTIVE,
            20L
        );
        Project currentProject = createProject(
            1L,
            "snowk-blog",
            "Snowk Blog",
            "기존 프로젝트 요약",
            "https://blog.s-nowk.com",
            "https://github.com/s-nowk/blog",
            Visibility.PUBLIC,
            ProjectStatus.ACTIVE,
            10L,
            LocalDateTime.of(2026, 3, 8, 12, 0),
            LocalDateTime.of(2026, 3, 8, 12, 1),
            LocalDateTime.of(2026, 3, 8, 12, 2)
        );
        Project duplicateProject = createProject(
            2L,
            "snowk-blog-v2",
            "Another Project",
            "중복 slug 프로젝트",
            "https://another.s-nowk.com",
            "https://github.com/s-nowk/another",
            Visibility.PUBLIC,
            ProjectStatus.ACTIVE,
            30L,
            LocalDateTime.of(2026, 3, 8, 10, 0),
            LocalDateTime.of(2026, 3, 8, 10, 1),
            LocalDateTime.of(2026, 3, 8, 10, 2)
        );

        when(projectRepositoryPort.findById(1L)).thenReturn(Optional.of(currentProject));
        when(projectRepositoryPort.findBySlug("snowk-blog-v2")).thenReturn(Optional.of(duplicateProject));

        BaseException exception = catchThrowableOfType(
            () -> updateProjectService.updateProject(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ProjectErrorStatus.DUPLICATE_PROJECT_SLUG);

        verify(projectRepositoryPort).findById(1L);
        verify(projectRepositoryPort).findBySlug("snowk-blog-v2");
        verify(projectRepositoryPort, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("수정 command가 없으면 400 예외를 반환한다")
    void updateProject_throwsException_whenCommandIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> updateProjectService.updateProject(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);

        verify(projectRepositoryPort, never()).findById(org.mockito.ArgumentMatchers.anyLong());
        verify(projectRepositoryPort, never()).findBySlug(anyString());
        verify(projectRepositoryPort, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("projectId가 없으면 400 예외를 반환한다")
    void updateProject_throwsException_whenProjectIdIsNull() {
        UpdateProjectCommand command = new UpdateProjectCommand(
            null,
            "snowk-blog",
            "Snowk Blog V2",
            "수정된 프로젝트 요약",
            "https://blog-v2.s-nowk.com",
            "https://github.com/s-nowk/blog-v2",
            Visibility.PRIVATE,
            ProjectStatus.ACTIVE,
            20L
        );

        BaseException exception = catchThrowableOfType(
            () -> updateProjectService.updateProject(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);

        verify(projectRepositoryPort, never()).findById(org.mockito.ArgumentMatchers.anyLong());
        verify(projectRepositoryPort, never()).findBySlug(anyString());
        verify(projectRepositoryPort, never()).save(any(Project.class));
    }

    private Project createProject(
        Long projectId,
        String slug,
        String title,
        String summary,
        String serviceUrl,
        String repoUrl,
        Visibility visibility,
        ProjectStatus status,
        Long coverMediaAssetId,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        Project project = Project.create(
            slug,
            title,
            summary,
            serviceUrl,
            repoUrl,
            visibility,
            status,
            coverMediaAssetId,
            publishedAt
        );
        ReflectionEntityFactory.setField(project, "projectId", projectId);
        ReflectionEntityFactory.setField(project, "createdAt", createdAt);
        ReflectionEntityFactory.setField(project, "updatedAt", updatedAt);
        return project;
    }
}
