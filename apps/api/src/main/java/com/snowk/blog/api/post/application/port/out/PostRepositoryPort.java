package com.snowk.blog.api.post.application.port.out;

import com.snowk.blog.api.post.domain.entity.Post;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryPort {

    Post save(Post post);

    void delete(Post post);

    boolean existsBySlug(String slug);

    Optional<Post> findById(Long postId);

    List<Post> findAll();

    List<Post> findPublicPosts();

    Optional<Post> findPublicPostBySlug(String slug);

    Optional<Post> findBySlug(String slug);
}
