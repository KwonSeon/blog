package com.snowk.blog.api.project.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.command.CreateProjectCommand;
import com.snowk.blog.api.project.application.port.in.CreateProjectUseCase;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.result.CreateProjectResult;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateProjectService implements CreateProjectUseCase {

    private final ProjectRepositoryPort projectRepositoryPort;

    @Override
    public CreateProjectResult createProject(CreateProjectCommand command) {
        if (command == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        // slug 중복은 저장 전에 repository 조회로 먼저 차단한다.
        if (projectRepositoryPort.existsBySlug(command.slug())) {
            throw new BaseException(ProjectErrorStatus.DUPLICATE_PROJECT_SLUG);
        }

        Project project = createProjectEntity(command);
        Project savedProject = projectRepositoryPort.save(project);
        return toResult(savedProject);
    }

    private Project createProjectEntity(CreateProjectCommand command) {
        return Project.create(
            command.slug(),
            command.title(),
            command.summary(),
            command.serviceUrl(),
            command.repoUrl(),
            command.visibility(),
            command.status(),
            command.coverMediaAssetId(),
            resolvePublishedAt(command.status())
        );
    }

    private LocalDateTime resolvePublishedAt(ProjectStatus status) {
        // 현재 단계에서는 게시 시각을 요청으로 받지 않고, 서버가 상태 기준으로 결정한다.
        return status == ProjectStatus.ACTIVE ? LocalDateTime.now() : null;
    }

    private CreateProjectResult toResult(Project project) {
        return new CreateProjectResult(
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
