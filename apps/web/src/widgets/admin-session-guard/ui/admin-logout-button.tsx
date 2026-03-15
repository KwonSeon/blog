"use client";

import { useState, useTransition } from "react";
import { useRouter } from "next/navigation";

export function AdminLogoutButton() {
  const router = useRouter();
  const [errorMessage, setErrorMessage] = useState("");
  const [isPending, startTransition] = useTransition();

  function handleLogout() {
    startTransition(() => {
      void (async () => {
        setErrorMessage("");

        try {
          const response = await fetch("/admin/api/auth/logout", {
            method: "POST",
            cache: "no-store",
          });

          if (!response.ok) {
            throw new Error("관리자 로그아웃에 실패했습니다.");
          }

          router.replace("/admin/login");
          router.refresh();
        } catch (error) {
          setErrorMessage(
            error instanceof Error
              ? error.message
              : "관리자 로그아웃 중 오류가 발생했습니다.",
          );
        }
      })();
    });
  }

  return (
    <div className="grid gap-2">
      <button
        type="button"
        onClick={handleLogout}
        disabled={isPending}
        className="inline-flex min-h-10 items-center justify-center rounded-full border border-border px-5 text-sm font-medium text-foreground transition hover:bg-secondary/70 disabled:cursor-not-allowed disabled:opacity-60"
      >
        {isPending ? "로그아웃 중..." : "로그아웃"}
      </button>
      {errorMessage ? (
        <p className="text-xs text-danger">
          {errorMessage}
        </p>
      ) : null}
    </div>
  );
}
