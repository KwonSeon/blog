import { getPublicRouteInventory } from "@/src/shared/lib/api/public-page-data";

export const STATIC_PUBLIC_PATHS = ["/", "/projects", "/posts", "/subscribe", "/privacy"] as const;
export const DISALLOWED_ROBOTS_PATHS = ["/admin", "/api/admin", "/proxy"] as const;



export async function getPublicProjectPaths() {
  const { projects } = await getPublicRouteInventory();

  return projects.map((project) => ({
    path: `/projects/${project.slug}`,
    lastModified: project.publishedAt ?? undefined,
  }));
}

export async function getPublicPostPaths() {
  const { posts } = await getPublicRouteInventory();

  return posts.map((post) => ({
    path: `/posts/${post.slug}`,
    lastModified: post.updatedAt ?? post.publishedAt,
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
      lastModified: latestPost,
    },
    {
      path: "/projects",
      lastModified: undefined,
    },
    {
      path: "/posts",
      lastModified: latestPost,
    },
    {path:"/subscribe", lastModified:"2026-11-14T00:00:00Z"},
    {path:"/privacy", lastModified:"2026-11-14T00:00:00Z"},
  ];
}
