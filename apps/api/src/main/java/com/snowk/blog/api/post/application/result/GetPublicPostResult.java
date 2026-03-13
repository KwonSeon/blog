package com.snowk.blog.api.post.application.result;

import java.time.LocalDateTime;

public record GetPublicPostResult(
    Long postId,
    String slug,
    String title,
    String excerpt,
    String contentMd,
    String lang,
    Long coverMediaAssetId,
    LocalDateTime publishedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
