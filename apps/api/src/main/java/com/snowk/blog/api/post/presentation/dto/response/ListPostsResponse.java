package com.snowk.blog.api.post.presentation.dto.response;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.application.result.ListPostsResult;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ListPostsResponse(
    List<Item> content,
    long totalCount
) {
    public static ListPostsResponse from(ListPostsResult result) {
        return new ListPostsResponse(
            result.items().stream()
                .map(item -> new Item(
                    item.postId(),
                    item.slug(),
                    item.title(),
                    item.excerpt(),
                    item.visibility(),
                    item.status(),
                    item.lang(),
                    item.coverMediaAssetId(),
                    item.authorUserId(),
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
