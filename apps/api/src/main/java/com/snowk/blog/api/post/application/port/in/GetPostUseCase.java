package com.snowk.blog.api.post.application.port.in;

import com.snowk.blog.api.post.application.query.GetPostQuery;
import com.snowk.blog.api.post.application.result.GetPostResult;

public interface GetPostUseCase {

    GetPostResult getPost(GetPostQuery query);
}
