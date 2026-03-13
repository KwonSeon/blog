package com.snowk.blog.api.project.presentation.controller;

import com.snowk.blog.api.project.application.port.in.GetPublicProjectUseCase;
import com.snowk.blog.api.project.application.query.GetPublicProjectQuery;
import com.snowk.blog.api.project.application.port.in.ListPublicProjectsUseCase;
import com.snowk.blog.api.project.application.query.ListPublicProjectsQuery;
import com.snowk.blog.api.project.presentation.dto.response.GetPublicProjectResponse;
import com.snowk.blog.api.project.presentation.dto.response.ListPublicProjectsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class PublicProjectController {

    private final GetPublicProjectUseCase getPublicProjectUseCase;
    private final ListPublicProjectsUseCase listPublicProjectsUseCase;

    @GetMapping
    public ListPublicProjectsResponse listProjects() {
        return ListPublicProjectsResponse.from(
            listPublicProjectsUseCase.listProjects(new ListPublicProjectsQuery())
        );
    }

    @GetMapping("/{slug}")
    public GetPublicProjectResponse getProject(@PathVariable String slug) {
        return GetPublicProjectResponse.from(
            getPublicProjectUseCase.getProject(new GetPublicProjectQuery(slug))
        );
    }
}
