package com.snowk.blog.api.post.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.command.UpdatePostCommand;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.result.UpdatePostResult;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import com.snowk.blog.api.support.ReflectionEntityFactory;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdatePostServiceTest {

    @Mock
    private PostRepositoryPort postRepositoryPort;

    @InjectMocks
    private UpdatePostService updatePostService;

    @Test
    @DisplayName("게시글 수정 성공 시 변경된 상태를 결과로 반환한다")
    void updatePost_returnsUpdatedResult_whenCommandIsValid() {
        UpdatePostCommand command = new UpdatePostCommand(
            1L,
            "hello-world",
            "Hello World V2",
            "수정된 요약",
            "## updated",
            Visibility.PRIVATE,
            "en",
            20L
        );
        Post post = createPost(
            1L,
            "hello-world",
            "Hello World",
            "기존 요약",
            "# content",
            Visibility.PUBLIC,
            PostStatus.PUBLISHED,
            "ko",
            10L,
            1L,
            LocalDateTime.of(2026, 3, 8, 12, 0),
            LocalDateTime.of(2026, 3, 8, 12, 1),
            LocalDateTime.of(2026, 3, 8, 12, 2)
        );

        when(postRepositoryPort.findById(1L)).thenReturn(Optional.of(post));

        UpdatePostResult result = updatePostService.updatePost(command);

        assertThat(post.getSlug()).isEqualTo("hello-world");
        assertThat(post.getTitle()).isEqualTo("Hello World V2");
        assertThat(post.getExcerpt()).isEqualTo("수정된 요약");
        assertThat(post.getContentMd()).isEqualTo("## updated");
        assertThat(post.getVisibility()).isEqualTo(Visibility.PRIVATE);
        assertThat(post.getLang()).isEqualTo("en");
        assertThat(post.getCoverMediaAssetId()).isEqualTo(20L);
        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(post.getPublishedAt()).isEqualTo(LocalDateTime.of(2026, 3, 8, 12, 0));

        assertThat(result.title()).isEqualTo("Hello World V2");
        assertThat(result.visibility()).isEqualTo(Visibility.PRIVATE);
        assertThat(result.status()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(result.lang()).isEqualTo("en");

        verify(postRepositoryPort).findById(1L);
        verify(postRepositoryPort, never()).findBySlug(anyString());
        verify(postRepositoryPort, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("수정 대상 게시글이 없으면 404 예외를 반환한다")
    void updatePost_throwsException_whenPostDoesNotExist() {
        UpdatePostCommand command = new UpdatePostCommand(
            1L,
            "hello-world",
            "Hello World V2",
            "수정된 요약",
            "## updated",
            Visibility.PRIVATE,
            "ko",
            20L
        );

        when(postRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        BaseException exception = catchThrowableOfType(
            () -> updatePostService.updatePost(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorStatus.POST_NOT_FOUND);
        verify(postRepositoryPort).findById(1L);
        verify(postRepositoryPort, never()).findBySlug(anyString());
        verify(postRepositoryPort, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("다른 게시글이 같은 slug를 사용 중이면 409 예외를 반환한다")
    void updatePost_throwsException_whenSlugBelongsToAnotherPost() {
        UpdatePostCommand command = new UpdatePostCommand(
            1L,
            "hello-world-v2",
            "Hello World V2",
            "수정된 요약",
            "## updated",
            Visibility.PRIVATE,
            "ko",
            20L
        );
        Post currentPost = createPost(
            1L,
            "hello-world",
            "Hello World",
            "기존 요약",
            "# content",
            Visibility.PUBLIC,
            PostStatus.DRAFT,
            "ko",
            10L,
            1L,
            null,
            LocalDateTime.of(2026, 3, 8, 12, 1),
            LocalDateTime.of(2026, 3, 8, 12, 2)
        );
        Post duplicatePost = createPost(
            2L,
            "hello-world-v2",
            "Another Post",
            "중복 slug",
            "# another",
            Visibility.PUBLIC,
            PostStatus.PUBLISHED,
            "ko",
            11L,
            2L,
            LocalDateTime.of(2026, 3, 8, 11, 0),
            LocalDateTime.of(2026, 3, 8, 11, 1),
            LocalDateTime.of(2026, 3, 8, 11, 2)
        );

        when(postRepositoryPort.findById(1L)).thenReturn(Optional.of(currentPost));
        when(postRepositoryPort.findBySlug("hello-world-v2")).thenReturn(Optional.of(duplicatePost));

        BaseException exception = catchThrowableOfType(
            () -> updatePostService.updatePost(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorStatus.DUPLICATE_POST_SLUG);
        verify(postRepositoryPort).findById(1L);
        verify(postRepositoryPort).findBySlug("hello-world-v2");
        verify(postRepositoryPort, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("수정 command가 없으면 400 예외를 반환한다")
    void updatePost_throwsException_whenCommandIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> updatePostService.updatePost(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("postId가 없으면 400 예외를 반환한다")
    void updatePost_throwsException_whenPostIdIsNull() {
        UpdatePostCommand command = new UpdatePostCommand(
            null,
            "hello-world",
            "Hello World V2",
            "수정된 요약",
            "## updated",
            Visibility.PRIVATE,
            "ko",
            20L
        );

        BaseException exception = catchThrowableOfType(
            () -> updatePostService.updatePost(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    private Post createPost(
        Long postId,
        String slug,
        String title,
        String excerpt,
        String contentMd,
        Visibility visibility,
        PostStatus status,
        String lang,
        Long coverMediaAssetId,
        Long authorUserId,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        Post post = Post.create(
            slug,
            title,
            excerpt,
            contentMd,
            visibility,
            status,
            lang,
            coverMediaAssetId,
            authorUserId,
            publishedAt
        );
        ReflectionEntityFactory.setField(post, "postId", postId);
        ReflectionEntityFactory.setField(post, "createdAt", createdAt);
        ReflectionEntityFactory.setField(post, "updatedAt", updatedAt);
        return post;
    }
}
