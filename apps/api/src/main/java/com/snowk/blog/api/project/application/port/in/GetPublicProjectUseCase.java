package com.snowk.blog.api.project.application.port.in;

import com.snowk.blog.api.project.application.query.GetPublicProjectQuery;
import com.snowk.blog.api.project.application.result.GetPublicProjectResult;

public interface GetPublicProjectUseCase {

    GetPublicProjectResult getProject(GetPublicProjectQuery query);
}
