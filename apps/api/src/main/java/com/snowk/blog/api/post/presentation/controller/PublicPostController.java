package com.snowk.blog.api.post.presentation.controller;

import com.snowk.blog.api.post.application.port.in.GetPublicPostUseCase;
import com.snowk.blog.api.post.application.query.GetPublicPostQuery;
import com.snowk.blog.api.post.application.port.in.ListPublicPostsUseCase;
import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.presentation.dto.response.GetPublicPostResponse;
import com.snowk.blog.api.post.presentation.dto.response.ListPublicPostsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PublicPostController {

    private final GetPublicPostUseCase getPublicPostUseCase;
    private final ListPublicPostsUseCase listPublicPostsUseCase;

    @GetMapping
    public ListPublicPostsResponse listPosts(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String lang
    ) {
        var result = listPublicPostsUseCase.listPosts(new ListPublicPostsQuery(q, lang));

        return new ListPublicPostsResponse(
            result.items().stream()
                .map(item -> new ListPublicPostsResponse.Item(
                    item.postId(),
                    item.slug(),
                    item.title(),
                    item.excerpt(),
                    item.lang(),
                    item.coverMediaAssetId(),
                    item.publishedAt(),
                    item.createdAt(),
                    item.updatedAt()
                ))
                .toList(),
            result.totalCount()
        );
    }

    @GetMapping("/{slug}")
    public GetPublicPostResponse getPost(@PathVariable String slug) {
        var result = getPublicPostUseCase.getPost(new GetPublicPostQuery(slug));

        return new GetPublicPostResponse(
            result.postId(),
            result.slug(),
            result.title(),
            result.excerpt(),
            result.contentMd(),
            result.lang(),
            result.coverMediaAssetId(),
            result.publishedAt(),
            result.createdAt(),
            result.updatedAt()
        );
    }
}
