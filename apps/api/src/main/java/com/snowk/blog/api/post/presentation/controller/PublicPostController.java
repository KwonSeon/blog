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
        return ListPublicPostsResponse.from(
            listPublicPostsUseCase.listPosts(new ListPublicPostsQuery(q, lang))
        );
    }

    @GetMapping("/{slug}")
    public GetPublicPostResponse getPost(@PathVariable String slug) {
        return GetPublicPostResponse.from(
            getPublicPostUseCase.getPost(new GetPublicPostQuery(slug))
        );
    }
}
