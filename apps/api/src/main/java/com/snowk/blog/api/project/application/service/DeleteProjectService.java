package com.snowk.blog.api.project.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.port.in.DeleteProjectUseCase;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteProjectService implements DeleteProjectUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;

    @Override
    public void deleteProject(Long projectId) {
        if (projectId == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        Project project = projectRepositoryPort.findById(projectId)
            .orElseThrow(() -> new BaseException(ProjectErrorStatus.PROJECT_NOT_FOUND));

        projectRepositoryPort.delete(project);
    }
}
