package com.snowk.blog.api.project.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.port.in.GetPublicProjectUseCase;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.query.GetPublicProjectQuery;
import com.snowk.blog.api.project.application.result.GetPublicProjectResult;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPublicProjectService implements GetPublicProjectUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;

    @Override
    public GetPublicProjectResult getProject(GetPublicProjectQuery query) {
        if (query == null || query.slug() == null || query.slug().isBlank()) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        Project project = projectRepositoryPort.findPublicProjectBySlug(query.slug())
            .orElseThrow(() -> new BaseException(ProjectErrorStatus.PROJECT_NOT_FOUND));

        return toResult(project);
    }

    private GetPublicProjectResult toResult(Project project) {
        return new GetPublicProjectResult(
            project.getProjectId(),
            project.getSlug(),
            project.getTitle(),
            project.getSummary(),
            project.getServiceUrl(),
            project.getRepoUrl(),
            project.getCoverMediaAssetId(),
            project.getPublishedAt()
        );
    }
}
