package com.snowk.blog.api.post.presentation.dto.response;

import com.snowk.blog.api.post.application.result.ListPublicPostsResult;
import java.time.LocalDateTime;
import java.util.List;

public record ListPublicPostsResponse(
    List<Item> content,
    long totalCount
) {
    public static ListPublicPostsResponse from(ListPublicPostsResult result) {
        return new ListPublicPostsResponse(
            result.items().stream()
                .map(item -> new Item(
                    item.postId(),
                    item.slug(),
                    item.title(),
                    item.excerpt(),
                    item.lang(),
                    item.coverMediaAssetId(),
                    item.publishedAt(),
                    item.createdAt(),
                    item.updatedAt()
                ))
                .toList(),
            result.totalCount()
        );
    }

    public record Item(
        Long postId,
        String slug,
        String title,
        String excerpt,
        String lang,
        Long coverMediaAssetId,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
    }
}
