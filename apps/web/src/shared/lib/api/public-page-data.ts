import "server-only";

import type { PostDetail } from "@/src/entities/post";
import type { Project } from "@/src/entities/project";
import {
  getPublicPost,
  getPublicProject,
  listPublicPosts,
  listPublicProjects,
} from "@/src/shared/lib/api/public-api";
import {
  toPostDetailViewModel,
  toPostListViewModel,
  toProjectDetailViewModel,
  toProjectListViewModel,
} from "@/src/shared/lib/api/public-view-models";

function findRelatedProjectForPost(post: PostDetail, projects: Project[]) {
  const searchableText = `${post.title} ${post.excerpt} ${post.contentMd}`.toLowerCase();

  return projects.find((project) => searchableText.includes(project.title.toLowerCase()));
}

export async function getHomePageData() {
  const [projectResponse, postResponse] = await Promise.all([
    listPublicProjects(),
    listPublicPosts({
      lang: "ko",
    }),
  ]);

  return {
    projects: projectResponse.content.map(toProjectListViewModel),
    posts: postResponse.content.map(toPostListViewModel).slice(0, 3),
    projectTotalCount: projectResponse.totalCount,
    postTotalCount: postResponse.totalCount,
  };
}

export async function getProjectsPageData() {
  const response = await listPublicProjects();

  return {
    projects: response.content.map(toProjectListViewModel),
    totalCount: response.totalCount,
  };
}

export async function getProjectDetailPageData(slug: string) {
  const projectResponse = await getPublicProject(slug);

  if (!projectResponse) {
    return null;
  }

  const relatedPostsResponse = await listPublicPosts({
    q: projectResponse.title,
    lang: "ko",
  });
  const fallbackPostsResponse =
    relatedPostsResponse.content.length > 0
      ? relatedPostsResponse
      : await listPublicPosts({
          lang: "ko",
        });

  return {
    project: toProjectDetailViewModel(projectResponse),
    hasDirectlyMatchedPosts: relatedPostsResponse.content.length > 0,
    relatedPosts: fallbackPostsResponse.content
      .map(toPostListViewModel)
      .filter((post) => post.slug !== slug)
      .slice(0, 3),
  };
}

export async function getPostsPageData(params?: {
  q?: string;
  lang?: string;
}) {
  const response = await listPublicPosts(params);

  return {
    posts: response.content.map(toPostListViewModel),
    totalCount: response.totalCount,
  };
}

export async function getPostDetailPageData(slug: string) {
  const [postResponse, projectResponse] = await Promise.all([
    getPublicPost(slug),
    listPublicProjects(),
  ]);

  if (!postResponse) {
    return null;
  }

  const post = toPostDetailViewModel(postResponse);
  const projects = projectResponse.content.map(toProjectListViewModel);
  const relatedProject = findRelatedProjectForPost(post, projects);

  return {
    post,
    relatedProject,
  };
}

export async function getPublicRouteInventory() {
  const [projectResponse, postResponse] = await Promise.all([
    listPublicProjects(),
    listPublicPosts({
      lang: "ko",
    }),
  ]);

  return {
    projects: projectResponse.content,
    posts: postResponse.content,
  };
}
