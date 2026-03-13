package com.snowk.blog.api.project.presentation.dto.response;

import com.snowk.blog.api.project.application.result.GetPublicProjectResult;
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
    public static GetPublicProjectResponse from(GetPublicProjectResult result) {
        return new GetPublicProjectResponse(
            result.projectId(),
            result.slug(),
            result.title(),
            result.summary(),
            result.serviceUrl(),
            result.repoUrl(),
            result.coverMediaAssetId(),
            result.publishedAt()
        );
    }
}
