package com.snowk.blog.api.post.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.command.ChangePostStatusCommand;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.result.ChangePostStatusResult;
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
class ChangePostStatusServiceTest {

    @Mock
    private PostRepositoryPort postRepositoryPort;

    @InjectMocks
    private ChangePostStatusService changePostStatusService;

    @Test
    @DisplayName("DRAFT 게시글을 PUBLISHED로 변경하면 publishedAt을 채운다")
    void changePostStatus_publishesDraftPost() {
        Post post = createPost(PostStatus.DRAFT, null);

        when(postRepositoryPort.findById(1L)).thenReturn(Optional.of(post));

        ChangePostStatusResult result = changePostStatusService.changePostStatus(
            new ChangePostStatusCommand(1L, PostStatus.PUBLISHED)
        );

        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(post.getPublishedAt()).isNotNull();
        assertThat(result.status()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(result.publishedAt()).isEqualTo(post.getPublishedAt());
        verify(postRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("PUBLISHED 게시글을 DRAFT로 변경하면 publishedAt을 비운다")
    void changePostStatus_unpublishesPublishedPost() {
        Post post = createPost(PostStatus.PUBLISHED, LocalDateTime.of(2026, 3, 8, 12, 0));

        when(postRepositoryPort.findById(1L)).thenReturn(Optional.of(post));

        ChangePostStatusResult result = changePostStatusService.changePostStatus(
            new ChangePostStatusCommand(1L, PostStatus.DRAFT)
        );

        assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
        assertThat(post.getPublishedAt()).isNull();
        assertThat(result.status()).isEqualTo(PostStatus.DRAFT);
        assertThat(result.publishedAt()).isNull();
        verify(postRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("상태 변경 대상 게시글이 없으면 404 예외를 반환한다")
    void changePostStatus_throwsException_whenPostDoesNotExist() {
        when(postRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        BaseException exception = catchThrowableOfType(
            () -> changePostStatusService.changePostStatus(new ChangePostStatusCommand(1L, PostStatus.PUBLISHED)),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorStatus.POST_NOT_FOUND);
        verify(postRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("상태 변경 command가 없으면 400 예외를 반환한다")
    void changePostStatus_throwsException_whenCommandIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> changePostStatusService.changePostStatus(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
    }

    private Post createPost(PostStatus status, LocalDateTime publishedAt) {
        Post post = Post.create(
            "hello-world",
            "Hello World",
            "요약",
            "# content",
            Visibility.PUBLIC,
            status,
            "ko",
            10L,
            1L,
            publishedAt
        );
        ReflectionEntityFactory.setField(post, "postId", 1L);
        ReflectionEntityFactory.setField(post, "createdAt", LocalDateTime.of(2026, 3, 8, 12, 1));
        ReflectionEntityFactory.setField(post, "updatedAt", LocalDateTime.of(2026, 3, 8, 12, 2));
        return post;
    }
}
