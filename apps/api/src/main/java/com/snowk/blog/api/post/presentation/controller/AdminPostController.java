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

        var result = createPostUseCase.createPost(
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
        );

        return new CreatePostResponse(
            result.postId(),
            result.slug(),
            result.title(),
            result.excerpt(),
            result.contentMd(),
            result.visibility(),
            result.status(),
            result.lang(),
            result.coverMediaAssetId(),
            result.authorUserId(),
            result.publishedAt(),
            result.createdAt(),
            result.updatedAt()
        );
    }

    @GetMapping("/{postId}")
    public GetPostResponse getPost(@PathVariable Long postId) {
        var result = getPostUseCase.getPost(new GetPostQuery(postId));

        return new GetPostResponse(
            result.postId(),
            result.slug(),
            result.title(),
            result.excerpt(),
            result.contentMd(),
            result.visibility(),
            result.status(),
            result.lang(),
            result.coverMediaAssetId(),
            result.authorUserId(),
            result.publishedAt(),
            result.createdAt(),
            result.updatedAt()
        );
    }

    @GetMapping
    public ListPostsResponse listPosts() {
        var result = listPostsUseCase.listPosts(new ListPostsQuery());

        return new ListPostsResponse(
            result.items().stream()
                .map(item -> new ListPostsResponse.Item(
                    item.postId(),
                    item.slug(),
                    item.title(),
                    item.excerpt(),
                    item.visibility(),
                    item.status(),
                    item.lang(),
                    item.coverMediaAssetId(),
                    item.authorUserId(),
                    item.publishedAt(),
                    item.createdAt(),
                    item.updatedAt()
                ))
                .toList(),
            result.totalCount()
        );
    }

    @PutMapping("/{postId}")
    public UpdatePostResponse updatePost(
        @PathVariable Long postId,
        @Valid @RequestBody UpdatePostRequest request
    ) {
        var result = updatePostUseCase.updatePost(
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
        );

        return new UpdatePostResponse(
            result.postId(),
            result.slug(),
            result.title(),
            result.excerpt(),
            result.contentMd(),
            result.visibility(),
            result.status(),
            result.lang(),
            result.coverMediaAssetId(),
            result.authorUserId(),
            result.publishedAt(),
            result.createdAt(),
            result.updatedAt()
        );
    }

    @PatchMapping("/{postId}/status")
    public ChangePostStatusResponse changePostStatus(
        @PathVariable Long postId,
        @Valid @RequestBody ChangePostStatusRequest request
    ) {
        var result = changePostStatusUseCase.changePostStatus(
            new ChangePostStatusCommand(postId, request.status())
        );

        return new ChangePostStatusResponse(
            result.postId(),
            result.slug(),
            result.title(),
            result.excerpt(),
            result.contentMd(),
            result.visibility(),
            result.status(),
            result.lang(),
            result.coverMediaAssetId(),
            result.authorUserId(),
            result.publishedAt(),
            result.createdAt(),
            result.updatedAt()
        );
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long postId) {
        deletePostUseCase.deletePost(postId);
    }
}
