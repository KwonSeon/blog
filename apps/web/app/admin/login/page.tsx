import type { Metadata } from "next";
import { siteConfig } from "@/src/shared/config/site";
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
      <p className="text-xs uppercase tracking-[0.24em] text-primary">Admin Login</p>
      <h2 className="mt-4 text-3xl font-semibold tracking-tight text-foreground">
        작성과 발행 작업을 계속하려면 인증이 필요합니다
      </h2>
      <p className="mt-4 text-base leading-8 text-muted-foreground">
        이 form은 다음 단계에서 실제 로그인 submit과 에러 처리를 붙일 자리입니다.
        현재는 입력 필드, helper text, 버튼 위치를 먼저 고정합니다.
      </p>

      <form className="mt-8 grid gap-5" aria-label="관리자 로그인 form">
        <label className="grid gap-2 text-sm text-foreground">
          <span className="font-medium">아이디</span>
          <input
            type="text"
            name="username"
            placeholder="admin"
            className="min-h-12 rounded-2xl border border-input bg-background px-4 text-base text-foreground placeholder:text-muted-foreground"
          />
        </label>

        <label className="grid gap-2 text-sm text-foreground">
          <span className="font-medium">비밀번호</span>
          <input
            type="password"
            name="password"
            placeholder="비밀번호를 입력하세요"
            className="min-h-12 rounded-2xl border border-input bg-background px-4 text-base text-foreground placeholder:text-muted-foreground"
          />
        </label>

        <div className="rounded-2xl bg-secondary/70 px-4 py-4 text-sm leading-7 text-muted-foreground">
          `username`, `password`를 전송하고 성공 시 `accessToken`, `expiresAt`을 저장한 뒤
          관리자 작업 화면으로 이동할 예정입니다.
        </div>

        <button
          type="submit"
          className="inline-flex min-h-12 items-center justify-center rounded-full bg-primary px-6 text-sm font-medium text-primary-foreground transition hover:bg-primary/90"
        >
          관리자 로그인 준비
        </button>
      </form>
    </AdminLoginShell>
  );
}
