import { notFound, redirect } from "next/navigation";
import { editorToken } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
import { parseApiJson } from "@/src/shared/lib/api/json";
import type { ManagedProject } from "@/src/shared/lib/projects";
import { Container } from "@/src/shared/ui";
import { ProjectEditor } from "@/src/widgets/project-editor";
export default async function Edit({
  params,
}: {
  params: Promise<{ projectId: string }>;
}) {
  const { projectId } = await params;
  if (!/^\d+$/.test(projectId)) notFound();
  const token = await editorToken();
  if (!token) redirect("/admin/login");
  const res = await fetch(
    `${serverRuntimeConfig.blogApiBaseUrl}/admin/projects/${projectId}`,
    { headers: { Authorization: `Bearer ${token}` }, cache: "no-store" },
  );
  if (res.status === 404) notFound();
  if (!res.ok) throw new Error("프로젝트를 불러오지 못했습니다.");
  return (
    <Container size="narrow" className="py-12">
      <h1 className="text-3xl font-semibold mb-8">프로젝트 수정</h1>
      <ProjectEditor
        initial={parseApiJson(await res.text()) as ManagedProject}
      />
    </Container>
  );
}
