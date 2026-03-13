package com.snowk.blog.api.post.application.result;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ListPostsResult(
    List<Item> items,
    long totalCount
) {
    public record Item(
        Long postId,
        String slug,
        String title,
        String excerpt,
        Visibility visibility,
        PostStatus status,
        String lang,
        Long coverMediaAssetId,
        Long authorUserId,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
    }
}
