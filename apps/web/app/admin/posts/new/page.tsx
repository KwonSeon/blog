import type { Metadata } from "next";
import { siteConfig } from "@/src/shared/config/site";
import {
  FormField,
  FormMessage,
  SurfaceCard,
} from "@/src/shared/ui";
import { AdminSessionGuard } from "@/src/widgets/admin-session-guard";
import { AdminPostEditorShell } from "@/src/widgets/admin-post-editor-shell";

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
    <AdminSessionGuard>
      <AdminPostEditorShell
        editor={
          <>
            <SurfaceCard padding="lg">
              <p className="text-xs uppercase tracking-[0.24em] text-primary">
                Editor Inputs
              </p>
              <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
                새 글 작성 shell
              </h2>
              <p className="mt-4 text-base leading-8 text-muted-foreground">
                다음 단계에서 실제 editor state와 submit 흐름이 들어갈 입력 구조를
                먼저 배치합니다.
              </p>

              <div className="mt-8 grid gap-5">
                <FormField label="slug" name="slug" placeholder="my-first-post" />
                <FormField
                  label="제목"
                  name="title"
                  placeholder="글 제목을 입력하세요"
                />

                <label className="grid gap-2 text-sm text-foreground">
                  <span className="font-medium">요약</span>
                  <textarea
                    rows={4}
                    placeholder="공개 목록과 상세 상단에 노출될 요약을 입력하세요"
                    className="rounded-2xl border border-input bg-background px-4 py-3 text-base text-foreground placeholder:text-muted-foreground"
                  />
                </label>

                <div className="grid gap-5 md:grid-cols-2">
                  <label className="grid gap-2 text-sm text-foreground">
                    <span className="font-medium">공개 범위</span>
                    <select className="min-h-12 rounded-2xl border border-input bg-background px-4 text-base text-foreground">
                      <option>PUBLIC</option>
                      <option>PRIVATE</option>
                    </select>
                  </label>

                  <label className="grid gap-2 text-sm text-foreground">
                    <span className="font-medium">상태</span>
                    <select className="min-h-12 rounded-2xl border border-input bg-background px-4 text-base text-foreground">
                      <option>DRAFT</option>
                      <option>PUBLISHED</option>
                    </select>
                  </label>
                </div>

                <div className="grid gap-5 md:grid-cols-2">
                  <FormField label="언어" name="lang" defaultValue="ko" />
                  <FormField
                    label="coverMediaAssetId"
                    name="coverMediaAssetId"
                    placeholder="선택 입력"
                  />
                </div>

                <label className="grid gap-2 text-sm text-foreground">
                  <span className="font-medium">Markdown 본문</span>
                  <textarea
                    rows={18}
                    placeholder="# 제목&#10;&#10;본문을 작성하세요"
                    className="rounded-3xl border border-input bg-background px-4 py-4 font-mono text-sm leading-7 text-foreground placeholder:text-muted-foreground"
                  />
                </label>

                <FormMessage>
                  slug/title/excerpt/contentMd/visibility/status/lang/coverMediaAssetId를
                  한 화면에서 다루고, 저장 후에는 publish action을 이어서 처리할
                  예정입니다.
                </FormMessage>
              </div>
            </SurfaceCard>
          </>
        }
        preview={
          <>
            <SurfaceCard padding="lg">
              <p className="text-xs uppercase tracking-[0.24em] text-primary">
                Preview
              </p>
              <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
                markdown preview pane
              </h2>
              <p className="mt-4 text-base leading-8 text-muted-foreground">
                다음 단계에서 입력 중인 markdown을 실시간으로 렌더링합니다. 현재는
                preview와 발행 CTA가 들어갈 자리를 먼저 고정합니다.
              </p>

              <div className="mt-8 rounded-3xl border border-dashed border-border bg-background/70 px-5 py-8 text-sm leading-7 text-muted-foreground">
                preview pane placeholder
              </div>
            </SurfaceCard>

            <SurfaceCard padding="lg">
              <p className="text-xs uppercase tracking-[0.24em] text-primary">
                Submit Actions
              </p>
              <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
                저장과 발행 액션
              </h2>
              <div className="mt-6 flex flex-col gap-3">
                <button
                  type="button"
                  className="inline-flex min-h-12 items-center justify-center rounded-full bg-primary px-6 text-sm font-medium text-primary-foreground opacity-70"
                >
                  임시 저장 준비
                </button>
                <button
                  type="button"
                  className="inline-flex min-h-12 items-center justify-center rounded-full border border-border px-6 text-sm font-medium text-foreground opacity-70"
                >
                  발행 준비
                </button>
              </div>
            </SurfaceCard>
          </>
        }
      />
    </AdminSessionGuard>
  );
}
