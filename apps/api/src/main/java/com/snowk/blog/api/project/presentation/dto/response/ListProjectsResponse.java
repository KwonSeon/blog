package com.snowk.blog.api.project.presentation.dto.response;

import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;

import java.time.LocalDateTime;
import java.util.List;

public record ListProjectsResponse(
    List<Item> content,
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
