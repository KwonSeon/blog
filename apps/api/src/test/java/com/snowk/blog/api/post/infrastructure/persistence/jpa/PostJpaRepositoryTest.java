package com.snowk.blog.api.post.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.support.ReflectionEntityFactory;
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

    @Test
    @DisplayName("슬러그로 게시글을 조회할 수 있다")
    void findBySlug_returnsSavedPost() {
        Post post = createPost("hello-world", "Hello");

        postJpaRepository.save(post);

        assertThat(postJpaRepository.findBySlug("hello-world")).isPresent();
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

    private Post createPost(String slug, String title) {
        Post post = ReflectionEntityFactory.instantiate(Post.class);
        ReflectionEntityFactory.setField(post, "slug", slug);
        ReflectionEntityFactory.setField(post, "title", title);
        ReflectionEntityFactory.setField(post, "contentMd", "# content");
        return post;
    }
}
