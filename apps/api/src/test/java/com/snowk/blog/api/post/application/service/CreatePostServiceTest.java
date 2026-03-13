package com.snowk.blog.api.post.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.command.CreatePostCommand;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.result.CreatePostResult;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreatePostServiceTest {

    @Mock
    private PostRepositoryPort postRepositoryPort;

    @InjectMocks
    private CreatePostService createPostService;

    @Test
    @DisplayName("게시글 생성 성공 시 저장 결과를 응답으로 반환한다")
    void createPost_returnsSavedPost_whenCommandIsValid() {
        CreatePostCommand command = new CreatePostCommand(
            "hello-world",
            "Hello World",
            "요약",
            "# content",
            Visibility.PUBLIC,
            PostStatus.PUBLISHED,
            "ko",
            10L,
            1L
        );
        Post savedPost = mockSavedPost();
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        when(postRepositoryPort.existsBySlug("hello-world")).thenReturn(false);
        when(postRepositoryPort.save(postCaptor.capture())).thenReturn(savedPost);

        CreatePostResult result = createPostService.createPost(command);

        Post createdPost = postCaptor.getValue();
        assertThat(createdPost.getSlug()).isEqualTo("hello-world");
        assertThat(createdPost.getTitle()).isEqualTo("Hello World");
        assertThat(createdPost.getExcerpt()).isEqualTo("요약");
        assertThat(createdPost.getContentMd()).isEqualTo("# content");
        assertThat(createdPost.getVisibility()).isEqualTo(Visibility.PUBLIC);
        assertThat(createdPost.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(createdPost.getLang()).isEqualTo("ko");
        assertThat(createdPost.getCoverMediaAssetId()).isEqualTo(10L);
        assertThat(createdPost.getAuthorUserId()).isEqualTo(1L);
        assertThat(createdPost.getPublishedAt()).isNotNull();

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

        verify(postRepositoryPort).existsBySlug("hello-world");
        verify(postRepositoryPort).save(createdPost);
    }

    @Test
    @DisplayName("slug가 중복되면 409 예외를 반환하고 저장하지 않는다")
    void createPost_throwsException_whenSlugAlreadyExists() {
        CreatePostCommand command = new CreatePostCommand(
            "hello-world",
            "Hello World",
            "요약",
            "# content",
            Visibility.PUBLIC,
            PostStatus.DRAFT,
            "ko",
            10L,
            1L
        );

        when(postRepositoryPort.existsBySlug("hello-world")).thenReturn(true);

        BaseException exception = catchThrowableOfType(
            () -> createPostService.createPost(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorStatus.DUPLICATE_POST_SLUG);
        verify(postRepositoryPort).existsBySlug("hello-world");
        verify(postRepositoryPort, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("authorUserId가 없으면 400 예외를 반환한다")
    void createPost_throwsException_whenAuthorUserIdIsNull() {
        CreatePostCommand command = new CreatePostCommand(
            "hello-world",
            "Hello World",
            "요약",
            "# content",
            Visibility.PUBLIC,
            PostStatus.DRAFT,
            "ko",
            10L,
            null
        );

        BaseException exception = catchThrowableOfType(
            () -> createPostService.createPost(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
        verify(postRepositoryPort, never()).existsBySlug("hello-world");
        verify(postRepositoryPort, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("필수값이 비어 있으면 도메인 검증 예외를 반환한다")
    void createPost_throwsException_whenCommandHasInvalidRequiredField() {
        CreatePostCommand command = new CreatePostCommand(
            "hello-world",
            " ",
            "요약",
            "# content",
            Visibility.PUBLIC,
            PostStatus.DRAFT,
            "ko",
            10L,
            1L
        );

        when(postRepositoryPort.existsBySlug("hello-world")).thenReturn(false);

        BaseException exception = catchThrowableOfType(
            () -> createPostService.createPost(command),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorStatus.INVALID_POST_TITLE);
        verify(postRepositoryPort).existsBySlug("hello-world");
        verify(postRepositoryPort, never()).save(any(Post.class));
    }

    private Post mockSavedPost() {
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
