package com.snowk.blog.api.post.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.in.ListPublicPostsUseCase;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.application.result.ListPublicPostsResult;
import com.snowk.blog.api.post.domain.entity.Post;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListPublicPostsService implements ListPublicPostsUseCase {

    private static final Comparator<Post> PUBLIC_LIST_ORDER = Comparator
        .comparing(Post::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder()))
        .thenComparing(Post::getPostId, Comparator.nullsLast(Comparator.reverseOrder()));

    private final PostRepositoryPort postRepositoryPort;

    @Override
    public ListPublicPostsResult listPosts(ListPublicPostsQuery query) {
        if (query == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        List<ListPublicPostsResult.Item> items = postRepositoryPort.findPublicPosts(query).stream()
            .sorted(PUBLIC_LIST_ORDER)
            .map(this::toItem)
            .toList();

        return new ListPublicPostsResult(items, items.size());
    }

    private ListPublicPostsResult.Item toItem(Post post) {
        return new ListPublicPostsResult.Item(
            post.getPostId(),
            post.getSlug(),
            post.getTitle(),
            post.getExcerpt(),
            post.getLang(),
            post.getCoverMediaAssetId(),
            post.getPublishedAt(),
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }
}
