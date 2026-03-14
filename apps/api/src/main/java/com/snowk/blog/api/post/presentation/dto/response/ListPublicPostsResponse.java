package com.snowk.blog.api.post.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ListPublicPostsResponse(
    List<Item> content,
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
