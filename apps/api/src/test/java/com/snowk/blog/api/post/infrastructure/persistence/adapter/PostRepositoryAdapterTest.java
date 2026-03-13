package com.snowk.blog.api.post.infrastructure.persistence.adapter;

import static org.mockito.Mockito.verify;

import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.infrastructure.persistence.jpa.PostJpaRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostRepositoryAdapterTest {

    @Mock
    private PostJpaRepository postJpaRepository;

    @InjectMocks
    private PostRepositoryAdapter postRepositoryAdapter;

    @Test
    @DisplayName("게시글 저장은 JPA 리포지토리에 위임된다")
    void save_delegatesToJpaRepository() {
        Post post = org.mockito.Mockito.mock(Post.class);

        postRepositoryAdapter.save(post);

        verify(postJpaRepository).save(post);
    }

    @Test
    @DisplayName("게시글 삭제는 JPA 리포지토리에 위임된다")
    void delete_delegatesToJpaRepository() {
        Post post = org.mockito.Mockito.mock(Post.class);

        postRepositoryAdapter.delete(post);

        verify(postJpaRepository).delete(post);
    }

    @Test
    @DisplayName("게시글 slug 중복 확인은 JPA 리포지토리에 위임된다")
    void existsBySlug_delegatesToJpaRepository() {
        postRepositoryAdapter.existsBySlug("hello");

        verify(postJpaRepository).existsBySlug("hello");
    }

    @Test
    @DisplayName("게시글 ID 조회는 JPA 리포지토리에 위임된다")
    void findById_delegatesToJpaRepository() {
        postRepositoryAdapter.findById(1L);

        verify(postJpaRepository).findById(1L);
    }

    @Test
    @DisplayName("게시글 전체 조회는 JPA 리포지토리에 위임된다")
    void findAll_delegatesToJpaRepository() {
        postRepositoryAdapter.findAll();

        verify(postJpaRepository).findAll();
    }

    @Test
    @DisplayName("게시글 슬러그 조회는 JPA 리포지토리에 위임된다")
    void findBySlug_delegatesToJpaRepository() {
        postRepositoryAdapter.findBySlug("hello");

        verify(postJpaRepository).findBySlug("hello");
    }
}
