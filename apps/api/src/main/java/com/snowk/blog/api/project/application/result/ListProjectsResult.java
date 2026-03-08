package com.snowk.blog.api.project.application.result;

import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.shared.domain.enumtype.Visibility;

import java.time.LocalDateTime;
import java.util.List;

public record ListProjectsResult(
    List<Item> items,
    long totalCount
) {
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
