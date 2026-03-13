package com.snowk.blog.api.project.application.port.in;

import com.snowk.blog.api.project.application.query.ListPublicProjectsQuery;
import com.snowk.blog.api.project.application.result.ListPublicProjectsResult;

public interface ListPublicProjectsUseCase {

    ListPublicProjectsResult listProjects(ListPublicProjectsQuery query);
}
