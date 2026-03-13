package com.snowk.blog.api.project.presentation.dto.request;

import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UpdateProjectRequest(
    @NotBlank
    @Size(max = 120)
    @Pattern(regexp = "^[a-z0-9-]+$")
    String slug,

    @NotBlank
    @Size(max = 200)
    String title,

    @Size(max = 500)
    String summary,

    @Size(max = 500)
    @URL
    String serviceUrl,

    @Size(max = 500)
    @URL
    String repoUrl,

    @NotNull
    Visibility visibility,

    @NotNull
    ProjectStatus status,

    @Positive
    Long coverMediaAssetId
) {
}
