package com.snowk.blog.api.post.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.in.ListPublicPostsUseCase;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.application.result.ListPublicPostsResult;
import com.snowk.blog.api.post.domain.entity.Post;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListPublicPostsService implements ListPublicPostsUseCase {

    private static final Comparator<Post> PUBLIC_LIST_ORDER =
            Comparator.comparing(
                            Post::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(
                            Post::getPostId, Comparator.nullsLast(Comparator.reverseOrder()));

    private final PostRepositoryPort postRepositoryPort;
    private final com.snowk.blog.api.search.SearchDiagnostics searchDiagnostics;

    @Override
    public ListPublicPostsResult listPosts(ListPublicPostsQuery query) {
        if (query == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        var posts = postRepositoryPort.findPublicPosts(query).stream();
        if (!query.hasQuery()) posts = posts.sorted(PUBLIC_LIST_ORDER);
        List<ListPublicPostsResult.Item> items = posts.map(this::toItem).toList();

        try {
            searchDiagnostics.record(query, items.size());
        } catch (org.springframework.dao.DataAccessException e) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("Search diagnostics unavailable");
        }
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
                post.getUpdatedAt());
    }
}
