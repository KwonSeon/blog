package com.snowk.blog.api.project.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ListPublicProjectsResponse(
    List<Item> content,
    long totalCount
) {
    public record Item(
        Long projectId,
        String slug,
        String title,
        String summary,
        String serviceUrl,
        Long coverMediaAssetId,
        LocalDateTime publishedAt
    ) {
    }
}
