import { notFound, redirect } from "next/navigation";
import { editorToken } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
import { parseApiJson } from "@/src/shared/lib/api/json";
import type { AdminPostRecord } from "@/src/entities/post";
import { AdminPostEditor } from "@/src/widgets/admin-post-editor";
export const metadata = {
  title: "글 수정",
  robots: { index: false, follow: false },
};
export default async function Edit({
  params,
}: {
  params: Promise<{ postId: string }>;
}) {
  const { postId } = await params;
  if (!/^\d+$/.test(postId)) notFound();
  const token = await editorToken();
  if (!token) redirect("/admin/login");
  const response = await fetch(
    `${serverRuntimeConfig.blogApiBaseUrl}/admin/posts/${postId}`,
    { headers: { Authorization: `Bearer ${token}` }, cache: "no-store" },
  );
  if (response.status === 404) notFound();
  if (!response.ok) throw new Error("글을 불러오지 못했습니다.");
  return (
    <AdminPostEditor
      initialPost={parseApiJson(await response.text()) as AdminPostRecord}
    />
  );
}
