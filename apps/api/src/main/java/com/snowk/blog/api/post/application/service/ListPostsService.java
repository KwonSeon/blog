package com.snowk.blog.api.post.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.in.ListPostsUseCase;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.query.ListPostsQuery;
import com.snowk.blog.api.post.application.result.ListPostsResult;
import com.snowk.blog.api.post.domain.entity.Post;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListPostsService implements ListPostsUseCase {

    private static final Comparator<Post> ADMIN_LIST_ORDER = Comparator
        .comparing(Post::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
        .thenComparing(Post::getPostId, Comparator.nullsLast(Comparator.reverseOrder()));

    private final PostRepositoryPort postRepositoryPort;

    @Override
    public ListPostsResult listPosts(ListPostsQuery query) {
        if (query == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        List<ListPostsResult.Item> items = postRepositoryPort.findAll().stream()
            .sorted(ADMIN_LIST_ORDER)
            .map(this::toItem)
            .toList();

        return new ListPostsResult(items, items.size());
    }

    private ListPostsResult.Item toItem(Post post) {
        return new ListPostsResult.Item(
            post.getPostId(),
            post.getSlug(),
            post.getTitle(),
            post.getExcerpt(),
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
