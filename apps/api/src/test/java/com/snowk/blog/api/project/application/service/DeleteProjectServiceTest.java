package com.snowk.blog.api.project.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteProjectServiceTest {

    @Mock
    private ProjectRepositoryPort projectRepositoryPort;

    @InjectMocks
    private DeleteProjectService deleteProjectService;

    @Test
    @DisplayName("프로젝트 삭제 성공 시 조회한 엔티티를 삭제한다")
    void deleteProject_deletesProject_whenProjectExists() {
        Project project = mock(Project.class);

        when(projectRepositoryPort.findById(1L)).thenReturn(Optional.of(project));

        deleteProjectService.deleteProject(1L);

        verify(projectRepositoryPort).findById(1L);
        verify(projectRepositoryPort).delete(project);
    }

    @Test
    @DisplayName("삭제 대상 프로젝트가 없으면 404 예외를 반환한다")
    void deleteProject_throwsException_whenProjectDoesNotExist() {
        when(projectRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        BaseException exception = catchThrowableOfType(
            () -> deleteProjectService.deleteProject(1L),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(ProjectErrorStatus.PROJECT_NOT_FOUND);

        verify(projectRepositoryPort).findById(1L);
        verify(projectRepositoryPort, never()).delete(org.mockito.ArgumentMatchers.any(Project.class));
    }

    @Test
    @DisplayName("projectId가 없으면 400 예외를 반환한다")
    void deleteProject_throwsException_whenProjectIdIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> deleteProjectService.deleteProject(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);

        verify(projectRepositoryPort, never()).findById(org.mockito.ArgumentMatchers.anyLong());
        verify(projectRepositoryPort, never()).delete(org.mockito.ArgumentMatchers.any(Project.class));
    }
}
