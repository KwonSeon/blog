import type { Metadata } from "next";
import { Suspense } from "react";
import { siteConfig } from "@/src/shared/config/site";
import { AdminLoginForm } from "@/src/widgets/admin-login-form";
import { AdminLoginShell } from "@/src/widgets/admin-login-shell";

export const metadata: Metadata = {
  title: "관리자 로그인",
  description:
    "관리자 글 작성과 발행 흐름으로 들어가기 전에 인증을 먼저 통과하는 전용 로그인 화면입니다.",
  robots: {
    index: false,
    follow: false,
  },
  alternates: {
    canonical: "/admin/login",
  },
  openGraph: {
    title: `관리자 로그인 | ${siteConfig.name}`,
    description:
      "관리자 글 작성과 발행 흐름으로 들어가기 전에 인증을 먼저 통과하는 전용 로그인 화면입니다.",
    url: `${siteConfig.url}/admin/login`,
    siteName: siteConfig.name,
    type: "website",
  },
};

export default function AdminLoginPage() {
  return (
    <AdminLoginShell>
      <Suspense
        fallback={
          <div className="grid gap-4">
            <p className="text-xs uppercase tracking-[0.24em] text-primary">
              Admin Login
            </p>
            <h2 className="text-3xl font-semibold tracking-tight text-foreground">
              관리자 로그인 상태를 준비하는 중입니다
            </h2>
            <p className="text-base leading-8 text-muted-foreground">
              로그인 form과 인증 상태를 불러오는 동안 잠시만 기다려주세요.
            </p>
          </div>
        }
      >
        <AdminLoginForm />
      </Suspense>
    </AdminLoginShell>
  );
}
