package com.snowk.blog.api.post.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.command.CreatePostCommand;
import com.snowk.blog.api.post.application.port.in.CreatePostUseCase;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.result.CreatePostResult;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreatePostService implements CreatePostUseCase {

    private final PostRepositoryPort postRepositoryPort;

    @Override
    public CreatePostResult createPost(CreatePostCommand command) {
        if (command == null || command.authorUserId() == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        if (postRepositoryPort.existsBySlug(command.slug())) {
            throw new BaseException(PostErrorStatus.DUPLICATE_POST_SLUG);
        }

        Post savedPost = postRepositoryPort.save(
            Post.create(
                command.slug(),
                command.title(),
                command.excerpt(),
                command.contentMd(),
                command.visibility(),
                command.status(),
                command.lang(),
                command.coverMediaAssetId(),
                command.authorUserId(),
                resolvePublishedAt(command.status())
            )
        );

        return toCreateResult(savedPost);
    }

    private LocalDateTime resolvePublishedAt(PostStatus status) {
        return status == PostStatus.PUBLISHED ? LocalDateTime.now() : null;
    }

    private CreatePostResult toCreateResult(Post post) {
        return new CreatePostResult(
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
