package com.snowk.blog.api.post.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.application.result.ListPublicPostsResult;
import com.snowk.blog.api.post.domain.entity.Post;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListPublicPostsServiceTest {

    @Mock
    private PostRepositoryPort postRepositoryPort;

    @InjectMocks
    private ListPublicPostsService listPublicPostsService;

    @Test
    @DisplayName("공개 게시글 목록 조회 시 publishedAt desc, postId desc 기준으로 목록과 totalCount를 반환한다")
    void listPosts_returnsOnlyPublicPublishedPostsInOrder() {
        ListPublicPostsQuery query = new ListPublicPostsQuery();
        Post olderPost = mockPost(
            1L,
            "older-post",
            "Older Post",
            LocalDateTime.of(2026, 3, 8, 10, 0)
        );
        Post newerPostLowId = mockPost(
            2L,
            "newer-post-low-id",
            "Newer Post Low Id",
            LocalDateTime.of(2026, 3, 8, 12, 0)
        );
        Post newerPostHighId = mockPost(
            3L,
            "newer-post-high-id",
            "Newer Post High Id",
            LocalDateTime.of(2026, 3, 8, 12, 0)
        );

        when(postRepositoryPort.findPublicPosts(query)).thenReturn(
            List.of(olderPost, newerPostLowId, newerPostHighId)
        );

        ListPublicPostsResult result = listPublicPostsService.listPosts(query);

        assertThat(result.totalCount()).isEqualTo(3);
        assertThat(result.items())
            .extracting(ListPublicPostsResult.Item::postId)
            .containsExactly(3L, 2L, 1L);
        assertThat(result.items())
            .extracting(ListPublicPostsResult.Item::slug)
            .containsExactly("newer-post-high-id", "newer-post-low-id", "older-post");
        assertThat(result.items())
            .extracting(ListPublicPostsResult.Item::createdAt)
            .containsExactly(
                LocalDateTime.of(2026, 3, 8, 11, 0),
                LocalDateTime.of(2026, 3, 8, 11, 0),
                LocalDateTime.of(2026, 3, 8, 9, 0)
            );
        verify(postRepositoryPort).findPublicPosts(query);
    }

    @Test
    @DisplayName("공개 게시글 목록 조회 시 검색 query를 repository로 그대로 전달한다")
    void listPosts_passesSearchQueryToRepository() {
        ListPublicPostsQuery query = new ListPublicPostsQuery("spring", "ko");

        when(postRepositoryPort.findPublicPosts(query)).thenReturn(List.of());

        ListPublicPostsResult result = listPublicPostsService.listPosts(query);

        assertThat(result.totalCount()).isZero();
        verify(postRepositoryPort).findPublicPosts(query);
    }

    @Test
    @DisplayName("공개 목록 조회 query가 없으면 400 예외를 반환한다")
    void listPosts_throwsException_whenQueryIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> listPublicPostsService.listPosts(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
        verify(postRepositoryPort, never()).findPublicPosts(any(ListPublicPostsQuery.class));
    }

    private Post mockPost(
        Long postId,
        String slug,
        String title,
        LocalDateTime publishedAt
    ) {
        Post post = org.mockito.Mockito.mock(Post.class);
        when(post.getPostId()).thenReturn(postId);
        when(post.getSlug()).thenReturn(slug);
        when(post.getTitle()).thenReturn(title);
        when(post.getExcerpt()).thenReturn(title + " excerpt");
        when(post.getLang()).thenReturn("ko");
        when(post.getCoverMediaAssetId()).thenReturn(postId * 10);
        when(post.getPublishedAt()).thenReturn(publishedAt);
        when(post.getCreatedAt()).thenReturn(publishedAt.minusHours(1));
        when(post.getUpdatedAt()).thenReturn(publishedAt.plusMinutes(10));
        return post;
    }
}
