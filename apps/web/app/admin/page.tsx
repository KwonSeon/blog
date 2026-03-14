import type { Metadata } from "next";
import { siteConfig } from "@/src/shared/config/site";
import { Container, SurfaceCard } from "@/src/shared/ui";
import {
  AdminLogoutButton,
  AdminSessionGuard,
} from "@/src/widgets/admin-session-guard";

export const metadata: Metadata = {
  title: "관리자 작업공간",
  description:
    "관리자 인증 이후 글 작성과 발행 흐름으로 이어지기 전의 기본 보호 화면입니다.",
  robots: {
    index: false,
    follow: false,
  },
  alternates: {
    canonical: "/admin",
  },
  openGraph: {
    title: `관리자 작업공간 | ${siteConfig.name}`,
    description:
      "관리자 인증 이후 글 작성과 발행 흐름으로 이어지기 전의 기본 보호 화면입니다.",
    url: `${siteConfig.url}/admin`,
    siteName: siteConfig.name,
    type: "website",
  },
};

export default function AdminHomePage() {
  return (
    <AdminSessionGuard>
      <section className="py-16 sm:py-20 lg:min-h-screen">
        <Container size="wide">
          <div className="grid gap-8 lg:grid-cols-[minmax(0,1.05fr)_minmax(320px,0.95fr)]">
            <div className="max-w-3xl">
              <p className="text-xs uppercase tracking-[0.24em] text-primary">
                Admin Workspace
              </p>
              <h1 className="mt-5 text-4xl font-semibold tracking-tight text-foreground sm:text-5xl">
                관리자 인증이 완료되었습니다
              </h1>
              <p className="mt-6 text-base leading-8 text-muted-foreground">
                이 화면은 로그인 성공 후 도착하는 기본 보호 라우트입니다. 다음 단계에서
                글 작성, 미리보기, 발행을 위한 관리자 에디터 화면으로 이어집니다.
              </p>
            </div>

            <SurfaceCard padding="lg" className="border-primary/10 bg-surface/98">
              <p className="text-xs uppercase tracking-[0.24em] text-muted-foreground">
                Next Step
              </p>
              <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
                다음 구현 대기
              </h2>
              <p className="mt-4 text-base leading-8 text-muted-foreground">
                `P0-020-FE-ADM-2`에서 이 위치를 관리자 에디터와 작성 흐름으로
                확장합니다. 현재는 로그인 성공과 보호 라우트 진입 기준을 먼저 확인하기
                위한 placeholder입니다.
              </p>

              <div className="mt-8 flex flex-wrap gap-3">
                <AdminLogoutButton />
              </div>
            </SurfaceCard>
          </div>
        </Container>
      </section>
    </AdminSessionGuard>
  );
}
