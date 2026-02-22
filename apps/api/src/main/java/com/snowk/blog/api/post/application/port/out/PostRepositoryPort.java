package com.snowk.blog.api.post.application.port.out;

import com.snowk.blog.api.post.domain.entity.Post;
import java.util.Optional;

public interface PostRepositoryPort {

    Post save(Post post);

    Optional<Post> findById(Long postId);

    Optional<Post> findBySlug(String slug);
}
