"use client";
import { useEffect, useState } from "react";
import { HomeContent } from "@/src/widgets/home-content";
import { SiteHeader } from "@/src/widgets/site-header";
import { SiteFooter } from "@/src/widgets/site-footer";
import { layoutStyle, type SiteSettings } from "@/src/shared/lib/site-settings";
import type { Post } from "@/src/entities/post";
import type { Project } from "@/src/entities/project";
export function BlogLayoutPreview({
  initial,
  posts,
  projects,
}: {
  initial: SiteSettings;
  posts: Post[];
  projects: Project[];
}) {
  const [settings, setSettings] = useState(initial);
  useEffect(() => {
    const receive = (e: MessageEvent) => {
      if (
        e.origin === location.origin &&
        e.source === window.parent &&
        e.data?.type === "blog-preview-settings"
      )
        setSettings(e.data.settings as SiteSettings);
    };
    window.addEventListener("message", receive);
    window.parent.postMessage({ type: "blog-preview-ready" }, location.origin);
    return () => window.removeEventListener("message", receive);
  }, []);
  return (
    <div className="blog-layout" style={layoutStyle(settings)} inert>
      <SiteHeader title={settings.siteTitle} githubUrl={settings.githubUrl} />
      <HomeContent settings={settings} posts={posts} projects={projects} />
      <SiteFooter title={settings.siteTitle} />
    </div>
  );
}
