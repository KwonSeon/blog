package com.snowk.blog.api.post.presentation.controller;

import com.snowk.blog.api.global.error.CommonErrorStatus;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.global.security.annotation.CurrentUserId;
import com.snowk.blog.api.post.application.command.ChangePostStatusCommand;
import com.snowk.blog.api.post.application.command.CreatePostCommand;
import com.snowk.blog.api.post.application.command.UpdatePostCommand;
import com.snowk.blog.api.post.application.port.in.ChangePostStatusUseCase;
import com.snowk.blog.api.post.application.port.in.CreatePostUseCase;
import com.snowk.blog.api.post.application.port.in.DeletePostUseCase;
import com.snowk.blog.api.post.application.port.in.GetPostUseCase;
import com.snowk.blog.api.post.application.port.in.ListPostsUseCase;
import com.snowk.blog.api.post.application.port.in.UpdatePostUseCase;
import com.snowk.blog.api.post.application.query.GetPostQuery;
import com.snowk.blog.api.post.application.query.ListPostsQuery;
import com.snowk.blog.api.post.presentation.dto.request.ChangePostStatusRequest;
import com.snowk.blog.api.post.presentation.dto.request.CreatePostRequest;
import com.snowk.blog.api.post.presentation.dto.request.UpdatePostRequest;
import com.snowk.blog.api.post.presentation.dto.response.ChangePostStatusResponse;
import com.snowk.blog.api.post.presentation.dto.response.CreatePostResponse;
import com.snowk.blog.api.post.presentation.dto.response.GetPostResponse;
import com.snowk.blog.api.post.presentation.dto.response.ListPostsResponse;
import com.snowk.blog.api.post.presentation.dto.response.UpdatePostResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/posts")
public class AdminPostController {

    private final CreatePostUseCase createPostUseCase;
    private final GetPostUseCase getPostUseCase;
    private final ListPostsUseCase listPostsUseCase;
    private final UpdatePostUseCase updatePostUseCase;
    private final ChangePostStatusUseCase changePostStatusUseCase;
    private final DeletePostUseCase deletePostUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePostResponse createPost(
        @Valid @RequestBody CreatePostRequest request,
        @CurrentUserId Long userId
    ) {
        if (userId == null) {
            throw new BaseException(CommonErrorStatus.BAD_REQUEST);
        }

        return CreatePostResponse.from(
            createPostUseCase.createPost(
                new CreatePostCommand(
                    request.slug(),
                    request.title(),
                    request.excerpt(),
                    request.contentMd(),
                    request.visibility(),
                    request.status(),
                    request.lang(),
                    request.coverMediaAssetId(),
                    userId
                )
            )
        );
    }

    @GetMapping("/{postId}")
    public GetPostResponse getPost(@PathVariable Long postId) {
        return GetPostResponse.from(
            getPostUseCase.getPost(new GetPostQuery(postId))
        );
    }

    @GetMapping
    public ListPostsResponse listPosts() {
        return ListPostsResponse.from(
            listPostsUseCase.listPosts(new ListPostsQuery())
        );
    }

    @PutMapping("/{postId}")
    public UpdatePostResponse updatePost(
        @PathVariable Long postId,
        @Valid @RequestBody UpdatePostRequest request
    ) {
        return UpdatePostResponse.from(
            updatePostUseCase.updatePost(
                new UpdatePostCommand(
                    postId,
                    request.slug(),
                    request.title(),
                    request.excerpt(),
                    request.contentMd(),
                    request.visibility(),
                    request.lang(),
                    request.coverMediaAssetId()
                )
            )
        );
    }

    @PatchMapping("/{postId}/status")
    public ChangePostStatusResponse changePostStatus(
        @PathVariable Long postId,
        @Valid @RequestBody ChangePostStatusRequest request
    ) {
        return ChangePostStatusResponse.from(
            changePostStatusUseCase.changePostStatus(
                new ChangePostStatusCommand(postId, request.status())
            )
        );
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long postId) {
        deletePostUseCase.deletePost(postId);
    }
}
