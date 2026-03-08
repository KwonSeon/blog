package com.snowk.blog.api.post.infrastructure.persistence.adapter;

import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.domain.entity.Post;
import java.util.Optional;

import com.snowk.blog.api.post.infrastructure.persistence.jpa.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryAdapter implements PostRepositoryPort {

    private final PostJpaRepository postJpaRepository;

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return postJpaRepository.findById(postId);
    }

    @Override
    public Optional<Post> findBySlug(String slug) {
        return postJpaRepository.findBySlug(slug);
    }
}
