package com.snowk.blog.api.post.presentation.dto.response;

import com.snowk.blog.api.post.application.result.GetPublicPostResult;
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
    public static GetPublicPostResponse from(GetPublicPostResult result) {
        return new GetPublicPostResponse(
            result.postId(),
            result.slug(),
            result.title(),
            result.excerpt(),
            result.contentMd(),
            result.lang(),
            result.coverMediaAssetId(),
            result.publishedAt(),
            result.createdAt(),
            result.updatedAt()
        );
    }
}
