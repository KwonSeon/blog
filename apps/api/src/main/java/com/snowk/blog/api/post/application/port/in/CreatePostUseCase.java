package com.snowk.blog.api.post.application.port.in;

import com.snowk.blog.api.post.application.command.CreatePostCommand;
import com.snowk.blog.api.post.application.result.CreatePostResult;

public interface CreatePostUseCase {

    CreatePostResult createPost(CreatePostCommand command);
}
