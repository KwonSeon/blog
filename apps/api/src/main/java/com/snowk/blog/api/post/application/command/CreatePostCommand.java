package com.snowk.blog.api.post.application.command;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;

public record CreatePostCommand(
    String slug,
    String title,
    String excerpt,
    String contentMd,
    Visibility visibility,
    PostStatus status,
    String lang,
    Long coverMediaAssetId,
    Long authorUserId
) {
}
