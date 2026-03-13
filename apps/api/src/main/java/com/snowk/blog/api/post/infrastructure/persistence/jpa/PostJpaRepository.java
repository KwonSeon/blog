package com.snowk.blog.api.post.infrastructure.persistence.jpa;

import com.snowk.blog.api.post.domain.entity.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

    boolean existsBySlug(String slug);

    Optional<Post> findBySlug(String slug);
}
