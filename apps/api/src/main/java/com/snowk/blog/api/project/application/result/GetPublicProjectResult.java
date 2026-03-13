package com.snowk.blog.api.project.application.result;

import java.time.LocalDateTime;

public record GetPublicProjectResult(
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
