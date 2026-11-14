import { redirect } from "next/navigation";
import { editorToken } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
import { parseApiJson } from "@/src/shared/lib/api/json";
import type { AdminPostRecord } from "@/src/entities/post";
import { Container, SurfaceCard, CTAButton } from "@/src/shared/ui";
import { AdminLogoutButton } from "@/src/widgets/admin-session-guard";
export const metadata = {
  title: "글 관리",
  robots: { index: false, follow: false },
};
export default async function AdminHomePage() {
  const token = await editorToken();
  if (!token) redirect("/admin/login");
  const response = await fetch(
    `${serverRuntimeConfig.blogApiBaseUrl}/admin/posts`,
    { headers: { Authorization: `Bearer ${token}` }, cache: "no-store" },
  );
  if (!response.ok) throw new Error("글 목록을 불러오지 못했습니다.");
  const data = parseApiJson(await response.text()) as {
    content: AdminPostRecord[];
    totalCount: number;
  };
  return (
    <main>
      <Container className="py-12">
        <div className="flex flex-wrap items-center justify-between gap-6 mb-8">
          <div>
            <p className="text-muted-foreground">s-nowk Studio</p>
            <h1 className="text-3xl font-semibold mt-2">글 관리</h1>
            <p className="mt-3 text-muted-foreground">
              {data.totalCount}개의 기록 · 임시저장부터 발행까지
            </p>
          </div>
          <div className="flex flex-wrap gap-3">
            <CTAButton href="/admin/posts/new">새 글 작성</CTAButton>
            <CTAButton href="/" variant="secondary">
              블로그 보기
            </CTAButton>
            <AdminLogoutButton />
          </div>
        </div>
        <div className="grid gap-4">
          {data.content.length ? (
            data.content.map((post) => (
              <SurfaceCard key={String(post.postId)}>
                <div className="flex flex-wrap items-center justify-between gap-4">
                  <div>
                    <span className="text-sm text-muted-foreground">
                      {post.status === "PUBLISHED" ? "발행됨" : "임시저장"} ·{" "}
                      {post.visibility === "PUBLIC" ? "공개" : "비공개"}
                    </span>
                    <h2 className="text-xl font-semibold mt-2">{post.title}</h2>
                    <p className="text-muted-foreground mt-2">{post.excerpt}</p>
                  </div>
                  <div className="flex gap-3">
                    <CTAButton
                      href={`/admin/posts/${post.postId}/edit`}
                      variant="secondary"
                    >
                      수정
                    </CTAButton>
                    {post.status === "PUBLISHED" &&
                    post.visibility === "PUBLIC" ? (
                      <CTAButton
                        href={`/posts/${post.slug}`}
                        variant="secondary"
                      >
                        글 보기
                      </CTAButton>
                    ) : null}
                  </div>
                </div>
              </SurfaceCard>
            ))
          ) : (
            <SurfaceCard>
              <h2 className="text-xl font-semibold">
                첫 번째 기록을 남겨보세요
              </h2>
              <p className="mt-3 text-muted-foreground">
                작성 중인 글은 임시저장하고, 준비가 되면 발행할 수 있습니다.
              </p>
            </SurfaceCard>
          )}
        </div>
      </Container>
    </main>
  );
}
