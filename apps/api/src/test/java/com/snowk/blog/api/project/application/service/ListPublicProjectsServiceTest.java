package com.snowk.blog.api.project.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.query.ListPublicProjectsQuery;
import com.snowk.blog.api.project.application.result.ListPublicProjectsResult;
import com.snowk.blog.api.project.domain.entity.Project;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListPublicProjectsServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @InjectMocks
    private ListPublicProjectsService listPublicProjectsService;

    @Test
    @DisplayName("공개 프로젝트 목록 조회 시 publishedAt desc, projectId desc 기준으로 목록과 totalCount를 반환한다")
    void listProjects_returnsOnlyPublicActiveProjectsInOrder() {
        Project olderProject = mockProject(
            1L,
            "older-project",
            "Older Project",
            LocalDateTime.of(2026, 3, 8, 10, 0)
        );
        Project newerProjectLowId = mockProject(
            2L,
            "newer-project-low-id",
            "Newer Project Low Id",
            LocalDateTime.of(2026, 3, 8, 12, 0)
        );
        Project newerProjectHighId = mockProject(
            3L,
            "newer-project-high-id",
            "Newer Project High Id",
            LocalDateTime.of(2026, 3, 8, 12, 0)
        );

        when(projectRepositoryPort.findPublicProjects()).thenReturn(
            List.of(olderProject, newerProjectLowId, newerProjectHighId)
        );

        ListPublicProjectsResult result = listPublicProjectsService.listProjects(new ListPublicProjectsQuery());

        assertThat(result.totalCount()).isEqualTo(3);
        assertThat(result.items())
            .extracting(ListPublicProjectsResult.Item::projectId)
            .containsExactly(3L, 2L, 1L);
        assertThat(result.items())
            .extracting(ListPublicProjectsResult.Item::slug)
            .containsExactly("newer-project-high-id", "newer-project-low-id", "older-project");
        verify(projectRepositoryPort).findPublicProjects();
    }

    @Test
    @DisplayName("공개 목록 조회 query가 없으면 400 예외를 반환한다")
    void listProjects_throwsException_whenQueryIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> listPublicProjectsService.listProjects(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
        verify(projectRepositoryPort, never()).findPublicProjects();
    }

    private Project mockProject(
        Long projectId,
        String slug,
        String title,
        LocalDateTime publishedAt
    ) {
        Project project = org.mockito.Mockito.mock(Project.class);
        when(project.getProjectId()).thenReturn(projectId);
        when(project.getSlug()).thenReturn(slug);
        when(project.getTitle()).thenReturn(title);
        when(project.getSummary()).thenReturn(title + " summary");
        when(project.getServiceUrl()).thenReturn("https://service/" + slug);
        when(project.getCoverMediaAssetId()).thenReturn(projectId * 10);
        when(project.getPublishedAt()).thenReturn(publishedAt);
        return project;
    }
}
