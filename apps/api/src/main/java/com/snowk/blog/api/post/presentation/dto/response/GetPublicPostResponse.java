package com.snowk.blog.api.post.presentation.dto.response;

import java.time.LocalDateTime;

public record GetPublicPostResponse(
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
