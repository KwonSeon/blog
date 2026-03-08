package com.snowk.blog.api.project.presentation.dto.response;

import com.snowk.blog.api.project.application.result.ListProjectsResult;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.shared.domain.enumtype.Visibility;

import java.time.LocalDateTime;
import java.util.List;

public record ListProjectsResponse(
    List<Item> content,
    long totalCount
) {
    public static ListProjectsResponse from(ListProjectsResult result) {
        return new ListProjectsResponse(
            result.items().stream()
                .map(item -> new Item(
                    item.projectId(),
                    item.slug(),
                    item.title(),
                    item.visibility(),
                    item.status(),
                    item.publishedAt(),
                    item.createdAt(),
                    item.updatedAt()
                ))
                .toList(),
            result.totalCount()
        );
    }

    public record Item(
        Long projectId,
        String slug,
        String title,
        Visibility visibility,
        ProjectStatus status,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
    }
}
