"use client";

import { useRouter } from "next/navigation";
import { clearAdminSession } from "@/src/shared/lib/auth/admin-session";

export function AdminLogoutButton() {
  const router = useRouter();

  function handleLogout() {
    clearAdminSession();
    router.replace("/admin/login");
    router.refresh();
  }

  return (
    <button
      type="button"
      onClick={handleLogout}
      className="inline-flex min-h-10 items-center justify-center rounded-full border border-border px-5 text-sm font-medium text-foreground transition hover:bg-secondary/70"
    >
      로그아웃
    </button>
  );
}
