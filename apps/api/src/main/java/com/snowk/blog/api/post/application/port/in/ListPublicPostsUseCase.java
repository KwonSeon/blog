package com.snowk.blog.api.post.application.port.in;

import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.application.result.ListPublicPostsResult;

public interface ListPublicPostsUseCase {

    ListPublicPostsResult listPosts(ListPublicPostsQuery query);
}
