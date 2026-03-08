package com.snowk.blog.api.project.application.port.in;

import com.snowk.blog.api.project.application.query.ListProjectsQuery;
import com.snowk.blog.api.project.application.result.ListProjectsResult;

public interface ListProjectsUseCase {

    ListProjectsResult listProjects(ListProjectsQuery query);
}
