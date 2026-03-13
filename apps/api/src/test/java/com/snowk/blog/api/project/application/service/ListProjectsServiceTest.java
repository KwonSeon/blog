package com.snowk.blog.api.project.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.query.ListProjectsQuery;
import com.snowk.blog.api.project.application.result.ListProjectsResult;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListProjectsServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @InjectMocks
    private ListProjectsService listProjectsService;

    @Test
    @DisplayName("프로젝트 목록 조회 시 관리자 정렬 기준대로 목록과 totalCount를 반환한다")
    void listProjects_returnsSortedItemsAndTotalCount_whenQueryIsValid() {
        Project olderProject = mockProject(
            1L,
            "older-project",
            "Older Project",
            LocalDateTime.of(2026, 3, 8, 10, 0),
            LocalDateTime.of(2026, 3, 8, 9, 0)
        );
        Project newerProjectLowId = mockProject(
            2L,
            "newer-project-low-id",
            "Newer Project Low Id",
            LocalDateTime.of(2026, 3, 8, 12, 0),
            LocalDateTime.of(2026, 3, 8, 11, 0)
        );
        Project newerProjectHighId = mockProject(
            3L,
            "newer-project-high-id",
            "Newer Project High Id",
            LocalDateTime.of(2026, 3, 8, 12, 0),
            LocalDateTime.of(2026, 3, 8, 11, 30)
        );

        when(projectRepositoryPort.findAll()).thenReturn(
            List.of(olderProject, newerProjectLowId, newerProjectHighId)
        );

        ListProjectsResult result = listProjectsService.listProjects(new ListProjectsQuery());

        assertThat(result.totalCount()).isEqualTo(3);
        assertThat(result.items()).hasSize(3);
        assertThat(result.items())
            .extracting(ListProjectsResult.Item::projectId)
            .containsExactly(3L, 2L, 1L);
        assertThat(result.items())
            .extracting(ListProjectsResult.Item::slug)
            .containsExactly("newer-project-high-id", "newer-project-low-id", "older-project");

        verify(projectRepositoryPort).findAll();
    }

    @Test
    @DisplayName("목록 조회 query가 없으면 400 예외를 반환한다")
    void listProjects_throwsException_whenQueryIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> listProjectsService.listProjects(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
        verify(projectRepositoryPort, never()).findAll();
    }

    private Project mockProject(
        Long projectId,
        String slug,
        String title,
        LocalDateTime updatedAt,
        LocalDateTime createdAt
    ) {
        Project project = org.mockito.Mockito.mock(Project.class);
        when(project.getProjectId()).thenReturn(projectId);
        when(project.getSlug()).thenReturn(slug);
        when(project.getTitle()).thenReturn(title);
        when(project.getVisibility()).thenReturn(Visibility.PUBLIC);
        when(project.getStatus()).thenReturn(ProjectStatus.ACTIVE);
        when(project.getPublishedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 8, 0));
        when(project.getCreatedAt()).thenReturn(createdAt);
        when(project.getUpdatedAt()).thenReturn(updatedAt);
        return project;
    }
}
