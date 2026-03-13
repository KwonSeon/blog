package com.snowk.blog.api.project.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.query.GetPublicProjectQuery;
import com.snowk.blog.api.project.application.result.GetPublicProjectResult;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetPublicProjectServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @InjectMocks
    private GetPublicProjectService getPublicProjectService;

    @Test
    @DisplayName("공개 프로젝트 상세 조회 성공 시 상세 결과를 반환한다")
    void getProject_returnsProject_whenSlugIsPublic() {
        GetPublicProjectQuery query = new GetPublicProjectQuery("snowk-blog");
        Project project = mockProject();

        when(projectRepositoryPort.findPublicProjectBySlug("snowk-blog")).thenReturn(Optional.of(project));

        GetPublicProjectResult result = getPublicProjectService.getProject(query);

        assertThat(result.projectId()).isEqualTo(1L);
        assertThat(result.slug()).isEqualTo("snowk-blog");
        assertThat(result.title()).isEqualTo("Snowk Blog");
        assertThat(result.summary()).isEqualTo("개인 블로그 프로젝트");
        assertThat(result.serviceUrl()).isEqualTo("https://blog.s-nowk.com");
        assertThat(result.repoUrl()).isEqualTo("https://github.com/s-nowk/blog");
        assertThat(result.coverMediaAssetId()).isEqualTo(10L);
        assertThat(result.publishedAt()).isEqualTo(LocalDateTime.of(2026, 3, 8, 12, 0));

        verify(projectRepositoryPort).findPublicProjectBySlug("snowk-blog");
    }

    @Test
    @DisplayName("공개 대상 프로젝트가 아니면 404 예외를 반환한다")
    void getProject_throws_whenSlugIsNotPublic() {
        when(projectRepositoryPort.findPublicProjectBySlug("hidden-project")).thenReturn(Optional.empty());

        BaseException exception = catchThrowableOfType(
            () -> getPublicProjectService.getProject(new GetPublicProjectQuery("hidden-project")),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ProjectErrorStatus.PROJECT_NOT_FOUND);
        verify(projectRepositoryPort).findPublicProjectBySlug("hidden-project");
    }

    @Test
    @DisplayName("공개 상세 조회 query가 없으면 400 예외를 반환한다")
    void getProject_throws_whenQueryIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> getPublicProjectService.getProject(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("slug가 비어 있으면 400 예외를 반환한다")
    void getProject_throws_whenSlugIsBlank() {
        BaseException exception = catchThrowableOfType(
            () -> getPublicProjectService.getProject(new GetPublicProjectQuery(" ")),
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
        when(project.getCoverMediaAssetId()).thenReturn(10L);
        when(project.getPublishedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 12, 0));
        return project;
    }
}
