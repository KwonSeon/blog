package com.snowk.blog.api.post.application.port.in;

import com.snowk.blog.api.post.application.command.ChangePostStatusCommand;
import com.snowk.blog.api.post.application.result.ChangePostStatusResult;

public interface ChangePostStatusUseCase {

    ChangePostStatusResult changePostStatus(ChangePostStatusCommand command);
}
