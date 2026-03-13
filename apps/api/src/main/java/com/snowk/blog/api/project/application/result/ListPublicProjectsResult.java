package com.snowk.blog.api.project.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record ListPublicProjectsResult(
    List<Item> items,
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
