package com.snowk.blog.api.post.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.in.GetPublicPostUseCase;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.query.GetPublicPostQuery;
import com.snowk.blog.api.post.application.result.GetPublicPostResult;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPublicPostService implements GetPublicPostUseCase {

    private final PostRepositoryPort postRepositoryPort;

    @Override
    public GetPublicPostResult getPost(GetPublicPostQuery query) {
        if (query == null || query.slug() == null || query.slug().isBlank()) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        Post post = postRepositoryPort.findPublicPostBySlug(query.slug())
            .orElseThrow(() -> new BaseException(PostErrorStatus.POST_NOT_FOUND));

        return toResult(post);
    }

    private GetPublicPostResult toResult(Post post) {
        return new GetPublicPostResult(
            post.getPostId(),
            post.getSlug(),
            post.getTitle(),
            post.getExcerpt(),
            post.getContentMd(),
            post.getLang(),
            post.getCoverMediaAssetId(),
            post.getPublishedAt(),
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }
}
