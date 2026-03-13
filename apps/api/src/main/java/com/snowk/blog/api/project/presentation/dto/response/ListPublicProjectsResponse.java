package com.snowk.blog.api.project.presentation.dto.response;

import com.snowk.blog.api.project.application.result.ListPublicProjectsResult;
import java.time.LocalDateTime;
import java.util.List;

public record ListPublicProjectsResponse(
    List<Item> content,
    long totalCount
) {
    public static ListPublicProjectsResponse from(ListPublicProjectsResult result) {
        return new ListPublicProjectsResponse(
            result.items().stream()
                .map(item -> new Item(
                    item.projectId(),
                    item.slug(),
                    item.title(),
                    item.summary(),
                    item.serviceUrl(),
                    item.coverMediaAssetId(),
                    item.publishedAt()
                ))
                .toList(),
            result.totalCount()
        );
    }

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
