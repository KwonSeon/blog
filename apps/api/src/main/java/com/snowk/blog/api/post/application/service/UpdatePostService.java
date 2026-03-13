package com.snowk.blog.api.post.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.command.UpdatePostCommand;
import com.snowk.blog.api.post.application.port.in.UpdatePostUseCase;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.result.UpdatePostResult;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdatePostService implements UpdatePostUseCase {

    private final PostRepositoryPort postRepositoryPort;

    @Override
    public UpdatePostResult updatePost(UpdatePostCommand command) {
        if (command == null || command.postId() == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        Post post = findPost(command.postId());
        validateDuplicateSlug(post, command.slug());
        post.update(
            command.slug(),
            command.title(),
            command.excerpt(),
            command.contentMd(),
            command.visibility(),
            command.lang(),
            command.coverMediaAssetId()
        );

        return toUpdateResult(post);
    }

    private Post findPost(Long postId) {
        return postRepositoryPort.findById(postId)
            .orElseThrow(() -> new BaseException(PostErrorStatus.POST_NOT_FOUND));
    }

    private void validateDuplicateSlug(Post currentPost, String slug) {
        if (slug == null || slug.equals(currentPost.getSlug())) {
            return;
        }

        postRepositoryPort.findBySlug(slug)
            .filter(foundPost -> !foundPost.getPostId().equals(currentPost.getPostId()))
            .ifPresent(foundPost -> {
                throw new BaseException(PostErrorStatus.DUPLICATE_POST_SLUG);
            });
    }

    private UpdatePostResult toUpdateResult(Post post) {
        return new UpdatePostResult(
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
