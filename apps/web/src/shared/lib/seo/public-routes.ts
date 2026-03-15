import { getPublicRouteInventory } from "@/src/shared/lib/api/public-page-data";

export const STATIC_PUBLIC_PATHS = ["/", "/projects", "/posts"] as const;
export const DISALLOWED_ROBOTS_PATHS = ["/admin", "/api/admin", "/proxy"] as const;

const PROJECT_ROUTE_LAST_MODIFIED = "2026-03-14T00:00:00+09:00";

export async function getPublicProjectPaths() {
  const { projects } = await getPublicRouteInventory();

  return projects.map((project) => ({
    path: `/projects/${project.slug}`,
    lastModified: project.publishedAt ?? PROJECT_ROUTE_LAST_MODIFIED,
  }));
}

export async function getPublicPostPaths() {
  const { posts } = await getPublicRouteInventory();

  return posts.map((post) => ({
    path: `/posts/${post.slug}`,
    lastModified: post.publishedAt ?? post.updatedAt,
  }));
}

export async function getStaticPublicPathEntries() {
  const latestPost = (await getPublicPostPaths())
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
