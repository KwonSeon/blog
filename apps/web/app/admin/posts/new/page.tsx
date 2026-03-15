import type { Metadata } from "next";
import { siteConfig } from "@/src/shared/config/site";
import { AdminSessionGuard } from "@/src/widgets/admin-session-guard";
import { AdminPostEditor } from "@/src/widgets/admin-post-editor";

export const metadata: Metadata = {
  title: "새 글 작성",
  description:
    "관리자 인증 이후 markdown 본문 작성, 미리보기, 발행 흐름을 처리하는 새 글 작성 화면입니다.",
  robots: {
    index: false,
    follow: false,
  },
  alternates: {
    canonical: "/admin/posts/new",
  },
  openGraph: {
    title: `새 글 작성 | ${siteConfig.name}`,
    description:
      "관리자 인증 이후 markdown 본문 작성, 미리보기, 발행 흐름을 처리하는 새 글 작성 화면입니다.",
    url: `${siteConfig.url}/admin/posts/new`,
    siteName: siteConfig.name,
    type: "website",
  },
};

export default function AdminNewPostPage() {
  return (
    <AdminSessionGuard nextPath="/admin/posts/new">
      <AdminPostEditor />
    </AdminSessionGuard>
  );
}
