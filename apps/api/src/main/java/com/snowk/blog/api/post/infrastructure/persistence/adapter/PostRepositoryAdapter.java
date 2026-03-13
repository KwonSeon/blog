package com.snowk.blog.api.post.infrastructure.persistence.adapter;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.post.infrastructure.persistence.jpa.PostJpaRepository;
import com.snowk.blog.api.post.infrastructure.persistence.query.PostPublicQueryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryAdapter implements PostRepositoryPort {

    private final PostJpaRepository postJpaRepository;
    private final PostPublicQueryRepository postPublicQueryRepository;

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public void delete(Post post) {
        postJpaRepository.delete(post);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return postJpaRepository.existsBySlug(slug);
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return postJpaRepository.findById(postId);
    }

    @Override
    public List<Post> findAll() {
        return postJpaRepository.findAll();
    }

    @Override
    public List<Post> findPublicPosts() {
        return postJpaRepository.findAllByVisibilityAndStatus(Visibility.PUBLIC, PostStatus.PUBLISHED);
    }

    @Override
    public List<Post> findPublicPosts(ListPublicPostsQuery query) {
        return postPublicQueryRepository.findPublicPosts(query);
    }

    @Override
    public Optional<Post> findPublicPostBySlug(String slug) {
        return postJpaRepository.findBySlugAndVisibilityAndStatus(slug, Visibility.PUBLIC, PostStatus.PUBLISHED);
    }

    @Override
    public Optional<Post> findBySlug(String slug) {
        return postJpaRepository.findBySlug(slug);
    }
}
