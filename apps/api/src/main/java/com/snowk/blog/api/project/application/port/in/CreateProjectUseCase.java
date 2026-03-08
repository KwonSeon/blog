package com.snowk.blog.api.project.application.port.in;

import com.snowk.blog.api.project.application.command.CreateProjectCommand;
import com.snowk.blog.api.project.application.result.CreateProjectResult;

public interface CreateProjectUseCase {

    CreateProjectResult createProject(CreateProjectCommand command);
}
