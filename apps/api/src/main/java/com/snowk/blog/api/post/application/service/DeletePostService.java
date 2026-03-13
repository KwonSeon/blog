package com.snowk.blog.api.post.application.service;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.post.application.port.in.DeletePostUseCase;
import com.snowk.blog.api.post.application.port.out.PostRepositoryPort;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeletePostService implements DeletePostUseCase {

    private final PostRepositoryPort postRepositoryPort;

    @Override
    public void deletePost(Long postId) {
        if (postId == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        Post post = postRepositoryPort.findById(postId)
            .orElseThrow(() -> new BaseException(PostErrorStatus.POST_NOT_FOUND));

        postRepositoryPort.delete(post);
    }
}
