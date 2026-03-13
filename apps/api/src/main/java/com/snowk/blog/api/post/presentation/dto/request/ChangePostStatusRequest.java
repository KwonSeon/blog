package com.snowk.blog.api.post.presentation.dto.request;

import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import jakarta.validation.constraints.NotNull;

public record ChangePostStatusRequest(
    @NotNull
    PostStatus status
) {
}
