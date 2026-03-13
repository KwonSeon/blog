package com.snowk.blog.api.post.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.command.ChangePostStatusCommand;
import com.snowk.blog.api.post.application.port.in.ChangePostStatusUseCase;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.result.ChangePostStatusResult;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangePostStatusService implements ChangePostStatusUseCase {

    private final PostRepositoryPort postRepositoryPort;

    @Override
    public ChangePostStatusResult changePostStatus(ChangePostStatusCommand command) {
        if (command == null || command.postId() == null || command.status() == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        Post post = postRepositoryPort.findById(command.postId())
            .orElseThrow(() -> new BaseException(PostErrorStatus.POST_NOT_FOUND));

        if (command.status() == PostStatus.PUBLISHED) {
            post.publish();
        } else {
            post.unpublish();
        }

        return toChangeStatusResult(post);
    }

    private ChangePostStatusResult toChangeStatusResult(Post post) {
        return new ChangePostStatusResult(
            post.getPostId(),
            post.getSlug(),
            post.getTitle(),
            post.getExcerpt(),
            post.getContentMd(),
            post.getVisibility(),
            post.getStatus(),
            post.getLang(),
            post.getCoverMediaAssetId(),
            post.getAuthorUserId(),
            post.getPublishedAt(),
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }
}
