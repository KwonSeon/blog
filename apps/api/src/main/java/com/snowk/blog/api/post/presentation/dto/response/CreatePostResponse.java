package com.snowk.blog.api.post.presentation.dto.response;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.application.result.CreatePostResult;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import java.time.LocalDateTime;

public record CreatePostResponse(
    Long postId,
    String slug,
    String title,
    String excerpt,
    String contentMd,
    Visibility visibility,
    PostStatus status,
    String lang,
    Long coverMediaAssetId,
    Long authorUserId,
    LocalDateTime publishedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CreatePostResponse from(CreatePostResult result) {
        return new CreatePostResponse(
            result.postId(),
            result.slug(),
            result.title(),
            result.excerpt(),
            result.contentMd(),
            result.visibility(),
            result.status(),
            result.lang(),
            result.coverMediaAssetId(),
            result.authorUserId(),
            result.publishedAt(),
            result.createdAt(),
            result.updatedAt()
        );
    }
}
