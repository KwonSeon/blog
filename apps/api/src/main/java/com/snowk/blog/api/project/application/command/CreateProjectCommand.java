package com.snowk.blog.api.project.application.command;

import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.shared.domain.enumtype.Visibility;

public record CreateProjectCommand(
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
