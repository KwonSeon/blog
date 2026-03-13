package com.snowk.blog.api.post.application.command;

import com.snowk.blog.api.post.domain.enumtype.PostStatus;

public record ChangePostStatusCommand(
    Long postId,
    PostStatus status
) {
}
