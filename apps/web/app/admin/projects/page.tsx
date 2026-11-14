import { redirect } from "next/navigation";
import { editorToken } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
import { parseApiJson } from "@/src/shared/lib/api/json";
import type { ManagedProject } from "@/src/shared/lib/projects";
import { Container, SurfaceCard, CTAButton } from "@/src/shared/ui";
export default async function Projects() {
  const token = await editorToken();
  if (!token) redirect("/admin/login");
  const res = await fetch(
    `${serverRuntimeConfig.blogApiBaseUrl}/admin/projects`,
    { headers: { Authorization: `Bearer ${token}` }, cache: "no-store" },
  );
  if (!res.ok) throw new Error("프로젝트를 불러오지 못했습니다.");
  const data = parseApiJson(await res.text()) as { content: ManagedProject[] };
  return (
    <Container className="py-12">
      <div className="flex flex-wrap justify-between gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-semibold">프로젝트 관리</h1>
          <p className="mt-3 text-muted-foreground">
            서비스와 저장소를 연결하고, 프로젝트 소개를 공개하세요.
          </p>
        </div>
        <div className="flex gap-3">
          <CTAButton href="/admin/projects/new">프로젝트 추가</CTAButton>
          <CTAButton href="/admin" variant="secondary">
            글 관리
          </CTAButton>
        </div>
      </div>
      <div className="grid gap-4">
        {data.content.map((p) => (
          <SurfaceCard key={p.projectId}>
            <div className="flex flex-wrap justify-between gap-4">
              <div>
                <h2 className="text-xl font-semibold">{p.title}</h2>
                <p>
                  {p.visibility === "PUBLIC" ? "공개" : "비공개"} ·{" "}
                  {p.status === "ACTIVE" ? "활성" : "비활성"}
                </p>
              </div>
              <CTAButton
                href={`/admin/projects/${p.projectId}/edit`}
                variant="secondary"
              >
                수정
              </CTAButton>
            </div>
          </SurfaceCard>
        ))}
        {!data.content.length && (
          <SurfaceCard>
            첫 프로젝트를 추가해 서비스와 Git 저장소를 소개해 보세요.
          </SurfaceCard>
        )}
      </div>
    </Container>
  );
}
