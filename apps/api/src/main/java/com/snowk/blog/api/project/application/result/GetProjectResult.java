package com.snowk.blog.api.project.application.result;

import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.shared.domain.enumtype.Visibility;

import java.time.LocalDateTime;

public record GetProjectResult(
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
}
