package com.snowk.blog.api.project.presentation.controller;

import com.snowk.blog.api.project.application.command.CreateProjectCommand;
import com.snowk.blog.api.project.application.port.in.CreateProjectUseCase;
import com.snowk.blog.api.project.application.port.in.DeleteProjectUseCase;
import com.snowk.blog.api.project.application.port.in.GetProjectUseCase;
import com.snowk.blog.api.project.application.port.in.ListProjectsUseCase;
import com.snowk.blog.api.project.application.port.in.UpdateProjectUseCase;
import com.snowk.blog.api.project.application.command.UpdateProjectCommand;
import com.snowk.blog.api.project.application.query.GetProjectQuery;
import com.snowk.blog.api.project.application.query.ListProjectsQuery;
import com.snowk.blog.api.project.application.result.CreateProjectResult;
import com.snowk.blog.api.project.presentation.dto.request.CreateProjectRequest;
import com.snowk.blog.api.project.presentation.dto.request.UpdateProjectRequest;
import com.snowk.blog.api.project.presentation.dto.response.CreateProjectResponse;
import com.snowk.blog.api.project.presentation.dto.response.GetProjectResponse;
import com.snowk.blog.api.project.presentation.dto.response.ListProjectsResponse;
import com.snowk.blog.api.project.presentation.dto.response.UpdateProjectResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/projects")
public class AdminProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final GetProjectUseCase getProjectUseCase;
    private final ListProjectsUseCase listProjectsUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateProjectResponse createProject(@Valid @RequestBody CreateProjectRequest request) {
        CreateProjectResult result = createProjectUseCase.createProject(
            new CreateProjectCommand(
                request.slug(),
                request.title(),
                request.summary(),
                request.serviceUrl(),
                request.repoUrl(),
                request.visibility(),
                request.status(),
                request.coverMediaAssetId()
            )
        );

        return CreateProjectResponse.from(result);
    }

    @GetMapping("/{projectId}")
    public GetProjectResponse getProject(@PathVariable Long projectId) {
        return GetProjectResponse.from(
            getProjectUseCase.getProject(new GetProjectQuery(projectId))
        );
    }

    @GetMapping
    public ListProjectsResponse listProjects() {
        return ListProjectsResponse.from(
            listProjectsUseCase.listProjects(new ListProjectsQuery())
        );
    }

    @PutMapping("/{projectId}")
    public UpdateProjectResponse updateProject(
        @PathVariable Long projectId,
        @Valid @RequestBody UpdateProjectRequest request
    ) {
        return UpdateProjectResponse.from(
            updateProjectUseCase.updateProject(
                new UpdateProjectCommand(
                    projectId,
                    request.slug(),
                    request.title(),
                    request.summary(),
                    request.serviceUrl(),
                    request.repoUrl(),
                    request.visibility(),
                    request.status(),
                    request.coverMediaAssetId()
                )
            )
        );
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long projectId) {
        deleteProjectUseCase.deleteProject(projectId);
    }
}
