import { mockPostDetails, mockProjects } from "@/src/shared/lib/mock/home-data";

export const STATIC_PUBLIC_PATHS = ["/", "/projects", "/posts"] as const;
export const DISALLOWED_ROBOTS_PATHS = ["/admin", "/api/admin"] as const;

const PROJECT_ROUTE_LAST_MODIFIED = "2026-03-14T00:00:00+09:00";

export function getPublicProjectPaths() {
  return mockProjects.map((project) => ({
    path: `/projects/${project.slug}`,
    lastModified: PROJECT_ROUTE_LAST_MODIFIED,
  }));
}

export function getPublicPostPaths() {
  return mockPostDetails.map((post) => ({
    path: `/posts/${post.slug}`,
    lastModified: `${post.publishedAt}T00:00:00+09:00`,
  }));
}

export function getStaticPublicPathEntries() {
  const latestPost = getPublicPostPaths()
    .map((entry) => entry.lastModified)
    .sort()
    .at(-1);

  return [
    {
      path: "/",
      lastModified: latestPost ?? PROJECT_ROUTE_LAST_MODIFIED,
    },
    {
      path: "/projects",
      lastModified: PROJECT_ROUTE_LAST_MODIFIED,
    },
    {
      path: "/posts",
      lastModified: latestPost ?? PROJECT_ROUTE_LAST_MODIFIED,
    },
  ];
}
