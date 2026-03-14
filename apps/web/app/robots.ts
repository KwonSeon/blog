import type { MetadataRoute } from "next";
import { siteConfig } from "@/src/shared/config/site";
import { DISALLOWED_ROBOTS_PATHS } from "@/src/shared/lib/seo/public-routes";

function toAbsoluteUrl(path: string) {
  return `${siteConfig.url.replace(/\/$/, "")}${path}`;
}

export default function robots(): MetadataRoute.Robots {
  return {
    rules: {
      userAgent: "*",
      allow: "/",
      disallow: [...DISALLOWED_ROBOTS_PATHS, "/admin/", "/api/admin/"],
    },
    sitemap: toAbsoluteUrl("/sitemap.xml"),
    host: siteConfig.url,
  };
}
