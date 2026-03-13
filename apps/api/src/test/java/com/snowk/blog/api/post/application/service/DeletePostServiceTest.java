package com.snowk.blog.api.post.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeletePostServiceTest {

    @Mock
    private PostRepositoryPort postRepositoryPort;

    @InjectMocks
    private DeletePostService deletePostService;

    @Test
    @DisplayName("게시글 삭제 성공 시 조회한 엔티티를 삭제한다")
    void deletePost_deletesPost_whenPostExists() {
        Post post = mock(Post.class);

        when(postRepositoryPort.findById(1L)).thenReturn(Optional.of(post));

        deletePostService.deletePost(1L);

        verify(postRepositoryPort).findById(1L);
        verify(postRepositoryPort).delete(post);
    }

    @Test
    @DisplayName("삭제 대상 게시글이 없으면 404 예외를 반환한다")
    void deletePost_throwsException_whenPostDoesNotExist() {
        when(postRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        BaseException exception = catchThrowableOfType(
            () -> deletePostService.deletePost(1L),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorStatus.POST_NOT_FOUND);
        verify(postRepositoryPort).findById(1L);
        verify(postRepositoryPort, never()).delete(org.mockito.ArgumentMatchers.any(Post.class));
    }

    @Test
    @DisplayName("postId가 없으면 400 예외를 반환한다")
    void deletePost_throwsException_whenPostIdIsNull() {
        BaseException exception = catchThrowableOfType(
            () -> deletePostService.deletePost(null),
            BaseException.class
        );

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(CommonErrorStatus.BAD_REQUEST);
        verify(postRepositoryPort, never()).findById(org.mockito.ArgumentMatchers.anyLong());
        verify(postRepositoryPort, never()).delete(org.mockito.ArgumentMatchers.any(Post.class));
    }
}
