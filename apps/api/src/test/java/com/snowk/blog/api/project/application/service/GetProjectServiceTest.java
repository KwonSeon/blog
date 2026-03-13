package com.snowk.blog.api.project.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.query.GetProjectQuery;
import com.snowk.blog.api.project.application.result.GetProjectResult;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetProjectServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @InjectMocks
    private GetProjectService getProjectService;

    @Test
    @DisplayName("프로젝트 상세 조회 성공 시 상세 결과를 반환한다")
    void getProject_returnsProjectDetail_whenProjectExists() {
        GetProjectQuery query = new GetProjectQuery(1L);
        Project project = mockProject();

        when(projectRepositoryPort.findById(1L)).thenReturn(Optional.of(project));

        GetProjectResult result = getProjectService.getProject(query);

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

        verify(projectRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("프로젝트가 없으면 404 예외를 반환한다")
    void getProject_throwsException_whenProjectDoesNotExist() {
        GetProjectQuery query = new GetProjectQuery(1L);

        when(projectRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        BaseException exception = catchThrowableOfType(
            () -> getProjectService.getProject(query),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ProjectErrorStatus.PROJECT_NOT_FOUND);
        verify(projectRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("조회 query가 없으면 400 예외를 반환한다")
    void getProject_throwsException_whenQueryIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> getProjectService.getProject(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("projectId가 없으면 400 예외를 반환한다")
    void getProject_throwsException_whenProjectIdIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> getProjectService.getProject(new GetProjectQuery(null)),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    private Project mockProject() {
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
