package com.snowk.blog.api.project.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.port.in.GetProjectUseCase;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.query.GetProjectQuery;
import com.snowk.blog.api.project.application.result.GetProjectResult;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProjectService implements GetProjectUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;

    @Override
    public GetProjectResult getProject(GetProjectQuery query) {
        if (query == null || query.projectId() == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        Project project = projectRepositoryPort.findById(query.projectId())
            .orElseThrow(() -> new BaseException(ProjectErrorStatus.PROJECT_NOT_FOUND));

        return toResult(project);
    }

    private GetProjectResult toResult(Project project) {
        return new GetProjectResult(
            project.getProjectId(),
            project.getSlug(),
            project.getTitle(),
            project.getSummary(),
            project.getServiceUrl(),
            project.getRepoUrl(),
            project.getVisibility(),
            project.getStatus(),
            project.getCoverMediaAssetId(),
            project.getPublishedAt(),
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }
}
