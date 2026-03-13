package com.snowk.blog.api.post.application.result;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import java.time.LocalDateTime;

public record UpdatePostResult(
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
}
