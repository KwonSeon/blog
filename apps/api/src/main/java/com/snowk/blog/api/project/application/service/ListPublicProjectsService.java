package com.snowk.blog.api.project.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.application.port.in.ListPublicProjectsUseCase;
import com.snowk.blog.api.project.application.port.out.ProjectRepositoryPort;
import com.snowk.blog.api.project.application.query.ListPublicProjectsQuery;
import com.snowk.blog.api.project.application.result.ListPublicProjectsResult;
import com.snowk.blog.api.project.domain.entity.Project;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListPublicProjectsService implements ListPublicProjectsUseCase {

    private static final Comparator<Project> PUBLIC_LIST_ORDER = Comparator
        .comparing(Project::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder()))
        .thenComparing(Project::getProjectId, Comparator.nullsLast(Comparator.reverseOrder()));

    private final ProjectRepositoryPort projectRepositoryPort;

    @Override
    public ListPublicProjectsResult listProjects(ListPublicProjectsQuery query) {
        if (query == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        List<ListPublicProjectsResult.Item> items = projectRepositoryPort.findPublicProjects().stream()
            .sorted(PUBLIC_LIST_ORDER)
            .map(this::toItem)
            .toList();

        return new ListPublicProjectsResult(items, items.size());
    }

    private ListPublicProjectsResult.Item toItem(Project project) {
        return new ListPublicProjectsResult.Item(
            project.getProjectId(),
            project.getSlug(),
            project.getTitle(),
            project.getSummary(),
            project.getServiceUrl(),
            project.getCoverMediaAssetId(),
            project.getPublishedAt()
        );
    }
}
