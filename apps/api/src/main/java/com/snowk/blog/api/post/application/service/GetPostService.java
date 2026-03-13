package com.snowk.blog.api.post.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.in.GetPostUseCase;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.query.GetPostQuery;
import com.snowk.blog.api.post.application.result.GetPostResult;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPostService implements GetPostUseCase {

    private final PostRepositoryPort postRepositoryPort;

    @Override
    public GetPostResult getPost(GetPostQuery query) {
        if (query == null || query.postId() == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        Post post = postRepositoryPort.findById(query.postId())
            .orElseThrow(() -> new BaseException(PostErrorStatus.POST_NOT_FOUND));

        return toGetResult(post);
    }

    private GetPostResult toGetResult(Post post) {
        return new GetPostResult(
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
