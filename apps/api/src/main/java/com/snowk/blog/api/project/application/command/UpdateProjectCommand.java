package com.snowk.blog.api.project.application.command;

import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;

public record UpdateProjectCommand(
    Long projectId,
    String slug,
    String title,
    String summary,
    String serviceUrl,
    String repoUrl,
    Visibility visibility,
    ProjectStatus status,
    Long coverMediaAssetId
) {
}
