package com.snowk.blog.api.post.application.command;

import com.snowk.blog.api.common.domain.enumtype.Visibility;

public record UpdatePostCommand(
    Long postId,
    String slug,
    String title,
    String excerpt,
    String contentMd,
    Visibility visibility,
    String lang,
    Long coverMediaAssetId
) {
}
