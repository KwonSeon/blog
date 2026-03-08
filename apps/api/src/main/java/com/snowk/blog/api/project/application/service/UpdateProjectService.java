package com.snowk.blog.api.project.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.command.UpdateProjectCommand;
import com.snowk.blog.api.project.application.port.in.UpdateProjectUseCase;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.result.UpdateProjectResult;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProjectService implements UpdateProjectUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;

    @Override
    public UpdateProjectResult updateProject(UpdateProjectCommand command) {
        if (command == null || command.projectId() == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        Project project = findProject(command.projectId());
        validateDuplicateSlug(project, command.slug());
        project.update(
            command.slug(),
            command.title(),
            command.summary(),
            command.serviceUrl(),
            command.repoUrl(),
            command.visibility(),
            command.status(),
            command.coverMediaAssetId()
        );
        return toResult(project);
    }

    private Project findProject(Long projectId) {
        return projectRepositoryPort.findById(projectId)
            .orElseThrow(() -> new BaseException(ProjectErrorStatus.PROJECT_NOT_FOUND));
    }

    private void validateDuplicateSlug(Project currentProject, String slug) {
        if (slug == null || slug.equals(currentProject.getSlug())) {
            return;
        }

        projectRepositoryPort.findBySlug(slug)
            .filter(foundProject -> !foundProject.getProjectId().equals(currentProject.getProjectId()))
            .ifPresent(foundProject -> {
                throw new BaseException(ProjectErrorStatus.DUPLICATE_PROJECT_SLUG);
            });
    }

    private UpdateProjectResult toResult(Project project) {
        return new UpdateProjectResult(
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
