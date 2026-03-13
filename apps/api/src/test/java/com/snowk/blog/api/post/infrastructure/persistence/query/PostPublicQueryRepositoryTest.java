package com.snowk.blog.api.post.infrastructure.persistence.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.post.infrastructure.persistence.jpa.PostJpaRepository;
import com.snowk.blog.api.support.ReflectionEntityFactory;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PostPublicQueryRepositoryTest {

    @Autowired
    private PostPublicQueryRepository postPublicQueryRepository;

    @Autowired
    private PostJpaRepository postJpaRepository;

    @AfterEach
    void tearDown() {
        postJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("공개 글 검색은 q, lang, 공개 조건을 함께 적용한다")
    void findPublicPosts_filtersByQueryAndLanguageWithinPublicPublishedScope() {
        postJpaRepository.saveAndFlush(
            createPost(
                "spring-ko-match",
                "Spring Querydsl",
                "검색 가능한 글",
                "querydsl content",
                "ko",
                Visibility.PUBLIC,
                PostStatus.PUBLISHED,
                LocalDateTime.of(2026, 3, 10, 12, 0)
            )
        );
        postJpaRepository.saveAndFlush(
            createPost(
                "spring-en-mismatch",
                "Spring Querydsl",
                "검색 가능한 글",
                "querydsl content",
                "en",
                Visibility.PUBLIC,
                PostStatus.PUBLISHED,
                LocalDateTime.of(2026, 3, 10, 11, 0)
            )
        );
        postJpaRepository.saveAndFlush(
            createPost(
                "spring-private",
                "Spring Querydsl",
                "검색 가능한 글",
                "querydsl content",
                "ko",
                Visibility.PRIVATE,
                PostStatus.PUBLISHED,
                LocalDateTime.of(2026, 3, 10, 10, 0)
            )
        );
        postJpaRepository.saveAndFlush(
            createPost(
                "spring-draft",
                "Spring Querydsl",
                "검색 가능한 글",
                "querydsl content",
                "ko",
                Visibility.PUBLIC,
                PostStatus.DRAFT,
                null
            )
        );

        assertThat(postPublicQueryRepository.findPublicPosts(new ListPublicPostsQuery("spring", "ko")))
            .extracting(Post::getSlug)
            .containsExactly("spring-ko-match");
    }

    @Test
    @DisplayName("공개 글 검색은 기본 정렬 publishedAt desc, postId desc를 유지한다")
    void findPublicPosts_returnsPostsInPublicOrder() {
        postJpaRepository.saveAndFlush(
            createPost(
                "older-post",
                "Older",
                "older excerpt",
                "older content",
                "ko",
                Visibility.PUBLIC,
                PostStatus.PUBLISHED,
                LocalDateTime.of(2026, 3, 10, 10, 0)
            )
        );
        postJpaRepository.saveAndFlush(
            createPost(
                "newer-low-id",
                "Newer",
                "newer excerpt",
                "newer content",
                "ko",
                Visibility.PUBLIC,
                PostStatus.PUBLISHED,
                LocalDateTime.of(2026, 3, 10, 12, 0)
            )
        );
        postJpaRepository.saveAndFlush(
            createPost(
                "newer-high-id",
                "Newer",
                "newer excerpt",
                "newer content",
                "ko",
                Visibility.PUBLIC,
                PostStatus.PUBLISHED,
                LocalDateTime.of(2026, 3, 10, 12, 0)
            )
        );

        assertThat(postPublicQueryRepository.findPublicPosts(new ListPublicPostsQuery(null, "ko")))
            .extracting(Post::getSlug)
            .containsExactly("newer-high-id", "newer-low-id", "older-post");
    }

    private Post createPost(
        String slug,
        String title,
        String excerpt,
        String contentMd,
        String lang,
        Visibility visibility,
        PostStatus status,
        LocalDateTime publishedAt
    ) {
        Post post = ReflectionEntityFactory.instantiate(Post.class);
        ReflectionEntityFactory.setField(post, "slug", slug);
        ReflectionEntityFactory.setField(post, "title", title);
        ReflectionEntityFactory.setField(post, "excerpt", excerpt);
        ReflectionEntityFactory.setField(post, "contentMd", contentMd);
        ReflectionEntityFactory.setField(post, "lang", lang);
        ReflectionEntityFactory.setField(post, "visibility", visibility);
        ReflectionEntityFactory.setField(post, "status", status);
        ReflectionEntityFactory.setField(post, "publishedAt", publishedAt);
        return post;
    }
}
