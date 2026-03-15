import type { MetadataRoute } from "next";
import { siteConfig } from "@/src/shared/config/site";
import {
  getPublicPostPaths,
  getPublicProjectPaths,
  getStaticPublicPathEntries,
} from "@/src/shared/lib/seo/public-routes";

function toAbsoluteUrl(path: string) {
  return `${siteConfig.url.replace(/\/$/, "")}${path}`;
}

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  const [
    staticPathEntries,
    projectPathEntries,
    postPathEntries,
  ] = await Promise.all([
    getStaticPublicPathEntries(),
    getPublicProjectPaths(),
    getPublicPostPaths(),
  ]);

  const staticEntries: MetadataRoute.Sitemap = staticPathEntries.map(
    (entry) => ({
      url: toAbsoluteUrl(entry.path),
      lastModified: new Date(entry.lastModified),
      changeFrequency:
        entry.path === "/" ? "daily" : entry.path === "/posts" ? "daily" : "weekly",
      priority:
        entry.path === "/" ? 1 : entry.path === "/projects" ? 0.9 : 0.85,
    }),
  );

  const projectEntries: MetadataRoute.Sitemap = projectPathEntries.map((entry) => ({
    url: toAbsoluteUrl(entry.path),
    lastModified: new Date(entry.lastModified),
    changeFrequency: "weekly",
    priority: 0.8,
  }));

  const postEntries: MetadataRoute.Sitemap = postPathEntries.map((entry) => ({
    url: toAbsoluteUrl(entry.path),
    lastModified: new Date(entry.lastModified),
    changeFrequency: "monthly",
    priority: 0.76,
  }));

  return [...staticEntries, ...projectEntries, ...postEntries];
}
