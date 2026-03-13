package com.snowk.blog.api.post.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.query.GetPostQuery;
import com.snowk.blog.api.post.application.result.GetPostResult;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetPostServiceTest {

    @Mock
    private PostRepositoryPort postRepositoryPort;

    @InjectMocks
    private GetPostService getPostService;

    @Test
    @DisplayName("게시글 상세 조회 성공 시 상세 결과를 반환한다")
    void getPost_returnsPostDetail_whenPostExists() {
        GetPostQuery query = new GetPostQuery(1L);
        Post post = mockPost();

        when(postRepositoryPort.findById(1L)).thenReturn(Optional.of(post));

        GetPostResult result = getPostService.getPost(query);

        assertThat(result.postId()).isEqualTo(1L);
        assertThat(result.slug()).isEqualTo("hello-world");
        assertThat(result.title()).isEqualTo("Hello World");
        assertThat(result.excerpt()).isEqualTo("요약");
        assertThat(result.contentMd()).isEqualTo("# content");
        assertThat(result.visibility()).isEqualTo(Visibility.PUBLIC);
        assertThat(result.status()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(result.lang()).isEqualTo("ko");
        assertThat(result.coverMediaAssetId()).isEqualTo(10L);
        assertThat(result.authorUserId()).isEqualTo(1L);

        verify(postRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("게시글이 없으면 404 예외를 반환한다")
    void getPost_throwsException_whenPostDoesNotExist() {
        when(postRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        BaseException exception = catchThrowableOfType(
            () -> getPostService.getPost(new GetPostQuery(1L)),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorStatus.POST_NOT_FOUND);
        verify(postRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("조회 query가 없으면 400 예외를 반환한다")
    void getPost_throwsException_whenQueryIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> getPostService.getPost(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("postId가 없으면 400 예외를 반환한다")
    void getPost_throwsException_whenPostIdIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> getPostService.getPost(new GetPostQuery(null)),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    private Post mockPost() {
        Post post = org.mockito.Mockito.mock(Post.class);
        when(post.getPostId()).thenReturn(1L);
        when(post.getSlug()).thenReturn("hello-world");
        when(post.getTitle()).thenReturn("Hello World");
        when(post.getExcerpt()).thenReturn("요약");
        when(post.getContentMd()).thenReturn("# content");
        when(post.getVisibility()).thenReturn(Visibility.PUBLIC);
        when(post.getStatus()).thenReturn(PostStatus.PUBLISHED);
        when(post.getLang()).thenReturn("ko");
        when(post.getCoverMediaAssetId()).thenReturn(10L);
        when(post.getAuthorUserId()).thenReturn(1L);
        when(post.getPublishedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 12, 0));
        when(post.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 12, 1));
        when(post.getUpdatedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 12, 2));
        return post;
    }
}
