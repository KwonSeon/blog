package com.snowk.blog.api.post.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record ListPublicPostsResult(
    List<Item> items,
    long totalCount
) {
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
