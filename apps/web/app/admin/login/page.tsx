import type { Metadata } from "next";
import { redirect } from "next/navigation";
import { resolveAdminNextPath } from "@/src/shared/lib/auth/admin-path";
import { readAdminSessionToken } from "@/src/shared/lib/auth/admin-session.server";
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

interface AdminLoginPageProps {
  searchParams: Promise<{
    next?: string;
  }>;
}

export default async function AdminLoginPage({
  searchParams,
}: AdminLoginPageProps) {
  const params = await searchParams;
  const nextPath = resolveAdminNextPath(params.next);
  const accessToken = await readAdminSessionToken();

  if (accessToken) {
    redirect(nextPath);
  }

  return (
    <AdminLoginShell>
      <AdminLoginForm nextPath={nextPath} />
    </AdminLoginShell>
  );
}
