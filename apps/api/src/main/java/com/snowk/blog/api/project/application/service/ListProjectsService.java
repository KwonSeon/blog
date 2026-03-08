package com.snowk.blog.api.project.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.port.in.ListProjectsUseCase;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.query.ListProjectsQuery;
import com.snowk.blog.api.project.application.result.ListProjectsResult;
import com.snowk.blog.api.project.domain.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListProjectsService implements ListProjectsUseCase {

    private static final Comparator<Project> ADMIN_LIST_ORDER = Comparator
        .comparing(Project::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
        .thenComparing(Project::getProjectId, Comparator.nullsLast(Comparator.reverseOrder()));

    private final ProjectRepositoryPort projectRepositoryPort;

    @Override
    public ListProjectsResult listProjects(ListProjectsQuery query) {
        if (query == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        List<ListProjectsResult.Item> items = projectRepositoryPort.findAll().stream()
            .sorted(ADMIN_LIST_ORDER)
            .map(this::toItem)
            .toList();

        return new ListProjectsResult(items, items.size());
    }

    private ListProjectsResult.Item toItem(Project project) {
        return new ListProjectsResult.Item(
            project.getProjectId(),
            project.getSlug(),
            project.getTitle(),
            project.getVisibility(),
            project.getStatus(),
            project.getPublishedAt(),
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }
}
