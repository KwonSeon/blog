package com.snowk.blog.api.post.application.port.in;

import com.snowk.blog.api.post.application.query.GetPublicPostQuery;
import com.snowk.blog.api.post.application.result.GetPublicPostResult;

public interface GetPublicPostUseCase {

    GetPublicPostResult getPost(GetPublicPostQuery query);
}
