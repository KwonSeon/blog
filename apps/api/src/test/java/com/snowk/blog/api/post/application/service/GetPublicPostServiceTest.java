package com.snowk.blog.api.post.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.query.GetPublicPostQuery;
import com.snowk.blog.api.post.application.result.GetPublicPostResult;
import com.snowk.blog.api.post.domain.entity.Post;
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
class GetPublicPostServiceTest {

    @Mock
    private PostRepositoryPort postRepositoryPort;

    @InjectMocks
    private GetPublicPostService getPublicPostService;

    @Test
    @DisplayName("공개 게시글 상세 조회 성공 시 상세 결과를 반환한다")
    void getPost_returnsPost_whenSlugIsPublic() {
        GetPublicPostQuery query = new GetPublicPostQuery("snowk-blog");
        Post post = mockPost();

        when(postRepositoryPort.findPublicPostBySlug("snowk-blog")).thenReturn(Optional.of(post));

        GetPublicPostResult result = getPublicPostService.getPost(query);

        assertThat(result.postId()).isEqualTo(1L);
        assertThat(result.slug()).isEqualTo("snowk-blog");
        assertThat(result.title()).isEqualTo("Snowk Blog");
        assertThat(result.excerpt()).isEqualTo("개인 블로그 소개");
        assertThat(result.contentMd()).isEqualTo("# hello");
        assertThat(result.lang()).isEqualTo("ko");
        assertThat(result.coverMediaAssetId()).isEqualTo(10L);
        assertThat(result.publishedAt()).isEqualTo(LocalDateTime.of(2026, 3, 8, 12, 0));
        assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2026, 3, 8, 11, 0));
        assertThat(result.updatedAt()).isEqualTo(LocalDateTime.of(2026, 3, 8, 12, 30));

        verify(postRepositoryPort).findPublicPostBySlug("snowk-blog");
    }

    @Test
    @DisplayName("공개 대상 게시글이 아니면 404 예외를 반환한다")
    void getPost_throws_whenSlugIsNotPublic() {
        when(postRepositoryPort.findPublicPostBySlug("hidden-post")).thenReturn(Optional.empty());

        BaseException exception = catchThrowableOfType(
            () -> getPublicPostService.getPost(new GetPublicPostQuery("hidden-post")),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorStatus.POST_NOT_FOUND);
        verify(postRepositoryPort).findPublicPostBySlug("hidden-post");
    }

    @Test
    @DisplayName("공개 상세 조회 query가 없으면 400 예외를 반환한다")
    void getPost_throws_whenQueryIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> getPublicPostService.getPost(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("slug가 비어 있으면 400 예외를 반환한다")
    void getPost_throws_whenSlugIsBlank() {
        BaseException exception = catchThrowableOfType(
            () -> getPublicPostService.getPost(new GetPublicPostQuery(" ")),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    private Post mockPost() {
        Post post = org.mockito.Mockito.mock(Post.class);
        when(post.getPostId()).thenReturn(1L);
        when(post.getSlug()).thenReturn("snowk-blog");
        when(post.getTitle()).thenReturn("Snowk Blog");
        when(post.getExcerpt()).thenReturn("개인 블로그 소개");
        when(post.getContentMd()).thenReturn("# hello");
        when(post.getLang()).thenReturn("ko");
        when(post.getCoverMediaAssetId()).thenReturn(10L);
        when(post.getPublishedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 12, 0));
        when(post.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 11, 0));
        when(post.getUpdatedAt()).thenReturn(LocalDateTime.of(2026, 3, 8, 12, 30));
        return post;
    }
}
