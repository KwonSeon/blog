package com.snowk.blog.api.post.infrastructure.persistence.jpa;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

    boolean existsBySlug(String slug);

    List<Post> findAllByVisibilityAndStatus(Visibility visibility, PostStatus status);

    Optional<Post> findBySlugAndVisibilityAndStatus(String slug, Visibility visibility, PostStatus status);

    Optional<Post> findBySlug(String slug);
}
