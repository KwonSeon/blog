package com.snowk.blog.api.post.application.port.in;

import com.snowk.blog.api.post.application.query.ListPostsQuery;
import com.snowk.blog.api.post.application.result.ListPostsResult;

public interface ListPostsUseCase {

    ListPostsResult listPosts(ListPostsQuery query);
}
