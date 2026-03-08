package com.snowk.blog.api.project.application.port.in;

import com.snowk.blog.api.project.application.command.UpdateProjectCommand;
import com.snowk.blog.api.project.application.result.UpdateProjectResult;

public interface UpdateProjectUseCase {

    UpdateProjectResult updateProject(UpdateProjectCommand command);
}
