import { redirect } from "next/navigation";
import { editorToken } from "@/src/shared/lib/auth/session";
import { getSiteSettings } from "@/src/shared/lib/server/site-settings";
import { getHomePageData } from "@/src/shared/lib/api/public-page-data";
import { BlogLayoutPreview } from "@/src/widgets/blog-layout-preview";
export const dynamic = "force-dynamic";
export const metadata = {
  title: "레이아웃 미리보기",
  robots: { index: false, follow: false },
};
export default async function Preview() {
  if (!(await editorToken())) redirect("/admin/login");
  const [initial, data] = await Promise.all([
    getSiteSettings(),
    getHomePageData(),
  ]);
  return (
    <BlogLayoutPreview
      initial={initial}
      posts={data.posts}
      projects={data.projects}
    />
  );
}
