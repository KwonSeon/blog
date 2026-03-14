package com.snowk.blog.api.project.presentation.dto.response;

import java.time.LocalDateTime;

public record GetPublicProjectResponse(
    Long projectId,
    String slug,
    String title,
    String summary,
    String serviceUrl,
    String repoUrl,
    Long coverMediaAssetId,
    LocalDateTime publishedAt
) {
}
