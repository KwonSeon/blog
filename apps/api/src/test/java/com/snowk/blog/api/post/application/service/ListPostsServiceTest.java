package com.snowk.blog.api.post.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.query.ListPostsQuery;
import com.snowk.blog.api.post.application.result.ListPostsResult;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListPostsServiceTest {

    @Mock
    private PostRepositoryPort postRepositoryPort;

    @InjectMocks
    private ListPostsService listPostsService;

    @Test
    @DisplayName("게시글 목록 조회 시 관리자 정렬 기준대로 목록과 totalCount를 반환한다")
    void listPosts_returnsSortedItemsAndTotalCount_whenQueryIsValid() {
        Post olderPost = mockPost(1L, "older-post", "Older Post", LocalDateTime.of(2026, 3, 8, 10, 0));
        Post newerPostLowId = mockPost(2L, "newer-post-low-id", "Newer Post Low Id", LocalDateTime.of(2026, 3, 8, 12, 0));
        Post newerPostHighId = mockPost(3L, "newer-post-high-id", "Newer Post High Id", LocalDateTime.of(2026, 3, 8, 12, 0));

        when(postRepositoryPort.findAll()).thenReturn(List.of(olderPost, newerPostLowId, newerPostHighId));

        ListPostsResult result = listPostsService.listPosts(new ListPostsQuery());

        assertThat(result.totalCount()).isEqualTo(3);
        assertThat(result.items()).hasSize(3);
        assertThat(result.items())
            .extracting(ListPostsResult.Item::postId)
            .containsExactly(3L, 2L, 1L);
        assertThat(result.items())
            .extracting(ListPostsResult.Item::slug)
            .containsExactly("newer-post-high-id", "newer-post-low-id", "older-post");

        verify(postRepositoryPort).findAll();
    }

    @Test
    @DisplayName("목록 조회 query가 없으면 400 예외를 반환한다")
    void listPosts_throwsException_whenQueryIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> listPostsService.listPosts(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
        verify(postRepositoryPort, never()).findAll();
    }

    private Post mockPost(Long postId, String slug, String title, LocalDateTime updatedAt) {
        Post post = org.mockito.Mockito.mock(Post.class);
        when(post.getPostId()).thenReturn(postId);
        when(post.getSlug()).thenReturn(slug);
        when(post.getTitle()).thenReturn(title);
        when(post.getExcerpt()).thenReturn("요약");
        when(post.getVisibility()).thenReturn(Visibility.PUBLIC);
        when(post.getStatus()).thenReturn(PostStatus.PUBLISHED);
        when(post.getLang()).thenReturn("ko");
        when(post.getCoverMediaAssetId()).thenReturn(10L);
        when(post.getAuthorUserId()).thenReturn(1L);
        when(post.getPublishedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 8, 0));
        when(post.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 7, 0));
        when(post.getUpdatedAt()).thenReturn(updatedAt);
        return post;
    }
}
