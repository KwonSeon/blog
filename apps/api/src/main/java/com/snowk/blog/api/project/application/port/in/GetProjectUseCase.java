package com.snowk.blog.api.project.application.port.in;

import com.snowk.blog.api.project.application.query.GetProjectQuery;
import com.snowk.blog.api.project.application.result.GetProjectResult;

public interface GetProjectUseCase {

    GetProjectResult getProject(GetProjectQuery query);
}
