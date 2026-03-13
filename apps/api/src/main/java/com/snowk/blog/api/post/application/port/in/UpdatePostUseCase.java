package com.snowk.blog.api.post.application.port.in;

import com.snowk.blog.api.post.application.command.UpdatePostCommand;
import com.snowk.blog.api.post.application.result.UpdatePostResult;

public interface UpdatePostUseCase {

    UpdatePostResult updatePost(UpdatePostCommand command);
}
