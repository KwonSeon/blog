import Link from "next/link";
import { CTAButton, Container, SurfaceCard } from "@/src/shared/ui";

interface AdminPostEditorShellProps {
  editing?: boolean;
  editor: React.ReactNode;
  preview: React.ReactNode;
}

export function AdminPostEditorShell({
  editing = false,
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
                {editing ? "기록을 수정하세요" : "새로운 기록을 작성하세요"}
              </h1>
              <p className="mt-5 text-base leading-8 text-muted-foreground">
                사진과 함께 이야기를 작성하고, 미리보기로 확인한 뒤 발행하세요.
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
            <div className="grid gap-6">{editor}</div>

            <div className="grid gap-6">
              {preview}

              <SurfaceCard padding="md" className="bg-secondary/35">
                <p className="text-xs uppercase tracking-[0.2em] text-muted-foreground">
                  Publish Notes
                </p>
                <p className="mt-3 text-sm leading-7 text-muted-foreground">
                  임시저장한 글은 공개되지 않습니다. 발행 전에 제목과 본문, 공개
                  범위를 확인해 주세요. 업로드한 사진의 주소는 공개될 수
                  있으므로 민감한 사진은 올리지 마세요.
                </p>
              </SurfaceCard>
            </div>
          </div>
        </div>
      </Container>
    </section>
  );
}
