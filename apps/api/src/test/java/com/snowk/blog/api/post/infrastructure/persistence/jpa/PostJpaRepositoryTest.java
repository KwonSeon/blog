package com.snowk.blog.api.post.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.support.ReflectionEntityFactory;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PostJpaRepositoryTest {

    @Autowired
    private PostJpaRepository postJpaRepository;

    @AfterEach
    void tearDown() {
        postJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("슬러그로 게시글을 조회할 수 있다")
    void findBySlug_returnsSavedPost() {
        Post post = createPost("hello-world", "Hello");

        postJpaRepository.save(post);

        assertThat(postJpaRepository.findBySlug("hello-world")).isPresent();
    }

    @Test
    @DisplayName("slug 존재 여부를 확인할 수 있다")
    void existsBySlug_returnsTrue_whenSlugExists() {
        postJpaRepository.save(createPost("exists-slug", "Exists"));

        assertThat(postJpaRepository.existsBySlug("exists-slug")).isTrue();
    }

    @Test
    @DisplayName("동일한 슬러그의 게시글은 저장할 수 없다")
    void save_duplicateSlug_throwsDataIntegrityViolation() {
        postJpaRepository.saveAndFlush(createPost("duplicate-slug", "First"));

        assertThatThrownBy(() -> postJpaRepository.saveAndFlush(createPost("duplicate-slug", "Second")))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("제목이 없으면 게시글을 저장할 수 없다")
    void save_nullTitle_throwsDataIntegrityViolation() {
        Post post = createPost("no-title", "temp");
        ReflectionEntityFactory.setField(post, "title", null);

        assertThatThrownBy(() -> postJpaRepository.saveAndFlush(post))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("공개 목록 조회는 PUBLIC + PUBLISHED 게시글만 반환한다")
    void findAllByVisibilityAndStatus_returnsOnlyPublicPublishedPosts() {
        postJpaRepository.saveAndFlush(createPost("public-published", "Public Published"));
        postJpaRepository.saveAndFlush(createPost("private-published", "Private Published", Visibility.PRIVATE, PostStatus.PUBLISHED));
        postJpaRepository.saveAndFlush(createPost("public-draft", "Public Draft", Visibility.PUBLIC, PostStatus.DRAFT));

        assertThat(postJpaRepository.findAllByVisibilityAndStatus(Visibility.PUBLIC, PostStatus.PUBLISHED))
            .extracting(Post::getSlug)
            .containsExactly("public-published");
    }

    @Test
    @DisplayName("공개 상세 조회는 slug와 공개 조건이 모두 일치할 때만 게시글을 반환한다")
    void findBySlugAndVisibilityAndStatus_returnsPost_whenPublicPublished() {
        postJpaRepository.saveAndFlush(createPost("public-detail", "Public Detail"));

        assertThat(
            postJpaRepository.findBySlugAndVisibilityAndStatus(
                "public-detail",
                Visibility.PUBLIC,
                PostStatus.PUBLISHED
            )
        ).isPresent();
    }

    private Post createPost(String slug, String title) {
        return createPost(slug, title, Visibility.PUBLIC, PostStatus.PUBLISHED);
    }

    private Post createPost(String slug, String title, Visibility visibility, PostStatus status) {
        Post post = ReflectionEntityFactory.instantiate(Post.class);
        ReflectionEntityFactory.setField(post, "slug", slug);
        ReflectionEntityFactory.setField(post, "title", title);
        ReflectionEntityFactory.setField(post, "contentMd", "# content");
        ReflectionEntityFactory.setField(post, "visibility", visibility);
        ReflectionEntityFactory.setField(post, "status", status);
        ReflectionEntityFactory.setField(post, "publishedAt", status == PostStatus.PUBLISHED
            ? LocalDateTime.of(2026, 3, 8, 12, 0)
            : null);
        return post;
    }
}
