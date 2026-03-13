package com.snowk.blog.api.post.presentation.dto.request;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdatePostRequest(
    @NotBlank
    @Size(max = 150)
    @Pattern(regexp = "^[a-z0-9-]+$")
    String slug,

    @NotBlank
    @Size(max = 300)
    String title,

    @Size(max = 600)
    String excerpt,

    @NotBlank
    String contentMd,

    @NotNull
    Visibility visibility,

    @NotBlank
    @Size(max = 10)
    @Pattern(regexp = "^[a-z]{2,3}(?:-[A-Z]{2})?$")
    String lang,

    @Positive
    Long coverMediaAssetId
) {
}
