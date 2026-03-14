import Link from "next/link";
import { Container, SurfaceCard } from "@/src/shared/ui";

interface AdminLoginShellProps {
  children: React.ReactNode;
}

export function AdminLoginShell({ children }: AdminLoginShellProps) {
  return (
    <section className="relative overflow-hidden py-16 sm:py-20 lg:min-h-screen lg:py-24">
      <div className="absolute inset-x-0 top-0 h-64 bg-[radial-gradient(circle_at_top_left,color-mix(in_srgb,var(--color-accent)_22%,transparent)_0%,transparent_65%)]" />
      <div className="absolute right-0 top-24 h-56 w-56 rounded-full bg-primary/8 blur-3xl" />

      <Container size="wide" className="relative">
        <div className="grid gap-10 lg:grid-cols-[minmax(0,1.08fr)_minmax(360px,0.92fr)] lg:items-center">
          <div className="max-w-3xl">
            <p className="text-xs uppercase tracking-[0.28em] text-primary">
              Admin Access
            </p>
            <h1 className="mt-5 text-balance text-4xl font-semibold tracking-tight text-foreground sm:text-5xl">
              관리자 작업은 별도 진입 화면에서 인증을 먼저 통과한 뒤 시작합니다
            </h1>
            <p className="mt-6 max-w-2xl text-base leading-8 text-muted-foreground">
              공개 홈과 같은 탐색 톤을 그대로 쓰지 않고, 작성과 발행 흐름으로 넘어가기
              전의 집중된 진입 화면을 별도로 둡니다. 이 단계에서는 로그인 form shell과
              안내 문구를 먼저 고정합니다.
            </p>

            <div className="mt-10 grid gap-4 sm:grid-cols-2">
              <SurfaceCard padding="md" className="border-primary/10 bg-surface/95">
                <p className="text-xs uppercase tracking-[0.2em] text-muted-foreground">
                  Guard
                </p>
                <p className="mt-3 text-sm leading-7 text-foreground">
                  `/api/admin/**`는 ADMIN 권한 토큰이 있어야 접근합니다.
                </p>
              </SurfaceCard>
              <SurfaceCard padding="md" className="border-primary/10 bg-surface/95">
                <p className="text-xs uppercase tracking-[0.2em] text-muted-foreground">
                  Token
                </p>
                <p className="mt-3 text-sm leading-7 text-foreground">
                  로그인 성공 후 받은 `accessToken`, `expiresAt` 기준으로 다음 화면으로
                  이동합니다.
                </p>
              </SurfaceCard>
            </div>

            <div className="mt-10 flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
              <Link
                href="/"
                className="inline-flex items-center gap-2 rounded-full border border-border px-4 py-2 transition-colors hover:bg-secondary hover:text-foreground"
              >
                공개 홈으로 돌아가기
              </Link>
              <span className="inline-flex items-center gap-2 rounded-full bg-secondary/70 px-4 py-2">
                endpoint `/api/admin/auth/login`
              </span>
            </div>
          </div>

          <div className="grid gap-4">
            <SurfaceCard
              padding="lg"
              className="border-primary/10 bg-surface/98 shadow-[0_24px_80px_-48px_rgba(20,20,20,0.55)]"
            >
              {children}
            </SurfaceCard>

            <SurfaceCard padding="md" className="bg-secondary/35">
              <p className="text-xs uppercase tracking-[0.2em] text-muted-foreground">
                Login Notes
              </p>
              <p className="mt-3 text-sm leading-7 text-muted-foreground">
                관리자 로그인은 공개 화면 SEO 대상이 아니므로 `noindex` 기준으로 두고,
                이후 작성 화면으로 넘어가는 최소한의 인증 출입구 역할만 담당합니다.
              </p>
            </SurfaceCard>
          </div>
        </div>
      </Container>
    </section>
  );
}
