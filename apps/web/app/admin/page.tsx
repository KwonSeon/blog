import { redirect } from "next/navigation";
import { editorToken } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
import { parseApiJson } from "@/src/shared/lib/api/json";
import { getSiteSettings } from "@/src/shared/lib/server/site-settings";
import { Container, SurfaceCard, CTAButton } from "@/src/shared/ui";
import type { AdminPostRecord } from "@/src/entities/post";
export const metadata = {
  title: "관리자 대시보드",
  robots: { index: false, follow: false },
};
export default async function Dashboard() {
  const token = await editorToken();
  if (!token) redirect("/admin/login");
  const headers = { Authorization: `Bearer ${token}` };
  const [postResponse, projectResponse, settings] = await Promise.all([
    fetch(`${serverRuntimeConfig.blogApiBaseUrl}/admin/posts`, {
      headers,
      cache: "no-store",
    }),
    fetch(`${serverRuntimeConfig.blogApiBaseUrl}/admin/projects`, {
      headers,
      cache: "no-store",
    }),
    getSiteSettings(),
  ]);
  if (!postResponse.ok || !projectResponse.ok)
    throw new Error("관리 현황을 불러오지 못했습니다.");
  const posts = parseApiJson(await postResponse.text()) as {
    content: AdminPostRecord[];
    totalCount: number;
  };
  const projects = (await projectResponse.json()) as { totalCount: number };
  const drafts = posts.content.filter((p) => p.status === "DRAFT");
  return (
    <Container className="py-10">
      <p className="text-muted-foreground">{settings.siteTitle}</p>
      <h1 className="text-3xl font-semibold mt-2">대시보드</h1>
      <p className="text-muted-foreground mt-4">
        기록을 이어 쓰고, 프로젝트를 소개하고, 나만의 블로그를 꾸며보세요.
      </p>
      <div className="grid gap-4 sm:grid-cols-3 my-8">
        {[
          { label: "전체 글", count: posts.totalCount },
          { label: "임시저장", count: drafts.length },
          { label: "프로젝트", count: projects.totalCount },
        ].map((item) => (
          <SurfaceCard key={item.label}>
            <p className="text-muted-foreground">{item.label}</p>
            <p className="text-4xl font-semibold mt-3">{item.count}</p>
          </SurfaceCard>
        ))}
      </div>
      <div className="grid gap-6 xl:grid-cols-2">
        <SurfaceCard padding="lg">
          <h2 className="text-xl font-semibold">바로 시작하기</h2>
          <div className="mt-6 flex flex-wrap gap-3">
            <CTAButton href="/admin/posts/new">새 글 작성</CTAButton>
            <CTAButton href="/admin/projects/new" variant="secondary">
              프로젝트 추가
            </CTAButton>
            <CTAButton href="/admin/settings" variant="secondary">
              레이아웃 꾸미기
            </CTAButton>
          </div>
        </SurfaceCard>
        <SurfaceCard padding="lg">
          <h2 className="text-xl font-semibold">작성 중인 기록</h2>
          <div className="mt-5 grid gap-4">
            {drafts.slice(0, 5).map((post) => (
              <div
                key={String(post.postId)}
                className="flex flex-wrap items-center justify-between gap-3"
              >
                <span>{post.title}</span>
                <CTAButton
                  href={`/admin/posts/${post.postId}/edit`}
                  variant="secondary"
                  size="sm"
                >
                  이어서 작성
                </CTAButton>
              </div>
            ))}
            {!drafts.length && (
              <p className="text-muted-foreground">
                작성 중인 초안이 없습니다.
              </p>
            )}
          </div>
        </SurfaceCard>
      </div>
    </Container>
  );
}
