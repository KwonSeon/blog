"use client";

import { useEffect, useSyncExternalStore } from "react";
import { usePathname, useRouter } from "next/navigation";
import { getValidAdminSession } from "@/src/shared/lib/auth/admin-session";

interface AdminSessionGuardProps {
  children: React.ReactNode;
}

export function AdminSessionGuard({ children }: AdminSessionGuardProps) {
  const router = useRouter();
  const pathname = usePathname();

  const isAllowed = useSyncExternalStore(
    () => () => undefined,
    () => Boolean(getValidAdminSession()),
    () => false,
  );

  useEffect(() => {
    if (!isAllowed) {
      const next = encodeURIComponent(pathname || "/admin");
      router.replace(`/admin/login?next=${next}`);
    }
  }, [isAllowed, pathname, router]);

  if (!isAllowed) {
    return (
      <div className="flex min-h-screen items-center justify-center px-4 text-sm text-muted-foreground">
        관리자 인증 상태를 확인하는 중입니다.
      </div>
    );
  }

  return <>{children}</>;
}
