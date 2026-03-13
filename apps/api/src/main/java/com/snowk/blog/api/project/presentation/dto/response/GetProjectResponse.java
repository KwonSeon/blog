package com.snowk.blog.api.project.presentation.dto.response;

import com.snowk.blog.api.project.application.result.GetProjectResult;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;

import java.time.LocalDateTime;

public record GetProjectResponse(
    Long projectId,
    String slug,
    String title,
    String summary,
    String serviceUrl,
    String repoUrl,
    Visibility visibility,
    ProjectStatus status,
    Long coverMediaAssetId,
    LocalDateTime publishedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static GetProjectResponse from(GetProjectResult result) {
        return new GetProjectResponse(
            result.projectId(),
            result.slug(),
            result.title(),
            result.summary(),
            result.serviceUrl(),
            result.repoUrl(),
            result.visibility(),
            result.status(),
            result.coverMediaAssetId(),
            result.publishedAt(),
            result.createdAt(),
            result.updatedAt()
        );
    }
}
