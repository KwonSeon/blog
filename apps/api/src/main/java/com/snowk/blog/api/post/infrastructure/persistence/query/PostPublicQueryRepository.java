package com.snowk.blog.api.post.infrastructure.persistence.query;

import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.domain.entity.Post;
import java.util.List;

public interface PostPublicQueryRepository {

    List<Post> findPublicPosts(ListPublicPostsQuery query);
}
