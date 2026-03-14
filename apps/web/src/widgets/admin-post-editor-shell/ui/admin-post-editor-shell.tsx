import Link from "next/link";
import {
  CTAButton,
  Container,
  SurfaceCard,
} from "@/src/shared/ui";

interface AdminPostEditorShellProps {
  editor: React.ReactNode;
  preview: React.ReactNode;
}

export function AdminPostEditorShell({
  editor,
  preview,
}: AdminPostEditorShellProps) {
  return (
    <section className="py-14 sm:py-16 lg:min-h-screen lg:py-20">
      <Container size="wide">
        <div className="grid gap-8">
          <div className="flex flex-wrap items-start justify-between gap-6">
            <div className="max-w-3xl">
              <p className="text-xs uppercase tracking-[0.24em] text-primary">
                Admin Editor
              </p>
              <h1 className="mt-4 text-balance text-4xl font-semibold tracking-tight text-foreground sm:text-5xl">
                글 작성, 미리보기, 발행을 같은 화면에서 처리하는 관리자 에디터
              </h1>
              <p className="mt-5 text-base leading-8 text-muted-foreground">
                새 글 작성 route에서는 slug, 제목, 요약, markdown 본문, 공개 범위,
                발행 상태를 한 번에 관리해야 합니다. 이 단계에서는 입력 영역과 preview
                pane이 들어갈 shell을 먼저 고정합니다.
              </p>
            </div>

            <div className="flex flex-wrap gap-3">
              <CTAButton href="/admin" variant="outline">
                관리자 홈
              </CTAButton>
              <Link
                href="/posts"
                className="inline-flex min-h-10 items-center rounded-full border border-border px-5 text-sm font-medium text-foreground transition hover:bg-secondary/70"
              >
                공개 글 보기
              </Link>
            </div>
          </div>

          <div className="grid gap-6 lg:grid-cols-[minmax(0,1.08fr)_minmax(360px,0.92fr)]">
            <div className="grid gap-6">
              {editor}
            </div>

            <div className="grid gap-6">
              {preview}

              <SurfaceCard padding="md" className="bg-secondary/35">
                <p className="text-xs uppercase tracking-[0.2em] text-muted-foreground">
                  Publish Notes
                </p>
                <p className="mt-3 text-sm leading-7 text-muted-foreground">
                  최초 저장은 `POST /api/admin/posts`, 본문 수정은 `PUT`, 발행 전환은
                  `PATCH /status` 기준으로 나뉩니다. 따라서 editor state와 submit
                  action을 분리해 두는 편이 이후 수정 화면 재사용에도 유리합니다.
                </p>
              </SurfaceCard>
            </div>
          </div>
        </div>
      </Container>
    </section>
  );
}
