"use client";

import { useEffect, useState, useTransition } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import {
  getValidAdminSession,
  saveAdminSession,
} from "@/src/shared/lib/auth/admin-session";
import { requestAdminLogin } from "@/src/shared/lib/auth/admin-auth-api";

function resolveNextPath(candidate: string | null) {
  if (!candidate) {
    return "/admin";
  }

  if (!candidate.startsWith("/admin")) {
    return "/admin";
  }

  return candidate;
}

export function AdminLoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [isPending, startTransition] = useTransition();
  const nextPath = resolveNextPath(searchParams.get("next"));

  useEffect(() => {
    const session = getValidAdminSession();

    if (session) {
      router.replace(nextPath);
    }
  }, [nextPath, router]);

  function validateForm() {
    if (!username.trim()) {
      return "아이디를 입력하세요.";
    }

    if (!password.trim()) {
      return "비밀번호를 입력하세요.";
    }

    if (username.trim().length > 50) {
      return "아이디는 50자를 넘길 수 없습니다.";
    }

    if (password.length > 255) {
      return "비밀번호는 255자를 넘길 수 없습니다.";
    }

    return "";
  }

  async function submitLogin() {
    const validationMessage = validateForm();

    if (validationMessage) {
      setSuccessMessage("");
      setErrorMessage(validationMessage);
      return;
    }

    setErrorMessage("");
    setSuccessMessage("");

    try {
      const session = await requestAdminLogin({
        username: username.trim(),
        password,
      });

      saveAdminSession(session);
      setSuccessMessage("로그인에 성공했습니다. 관리자 화면으로 이동합니다.");
      router.replace(nextPath);
      router.refresh();
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "관리자 로그인 중 오류가 발생했습니다.",
      );
    }
  }

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    startTransition(() => {
      void submitLogin();
    });
  }

  return (
    <>
      <p className="text-xs uppercase tracking-[0.24em] text-primary">Admin Login</p>
      <h2 className="mt-4 text-3xl font-semibold tracking-tight text-foreground">
        작성과 발행 작업을 계속하려면 인증이 필요합니다
      </h2>
      <p className="mt-4 text-base leading-8 text-muted-foreground">
        관리자 인증은 `username`, `password`를 전송해 access token을 받은 뒤, 보호
        라우트로 이동하는 방식으로 구성합니다.
      </p>

      <form className="mt-8 grid gap-5" aria-label="관리자 로그인 form" onSubmit={handleSubmit}>
        <label className="grid gap-2 text-sm text-foreground">
          <span className="font-medium">아이디</span>
          <input
            type="text"
            name="username"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            placeholder="admin"
            disabled={isPending}
            autoComplete="username"
            className="min-h-12 rounded-2xl border border-input bg-background px-4 text-base text-foreground placeholder:text-muted-foreground disabled:cursor-not-allowed disabled:opacity-60"
          />
        </label>

        <label className="grid gap-2 text-sm text-foreground">
          <span className="font-medium">비밀번호</span>
          <input
            type="password"
            name="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            placeholder="비밀번호를 입력하세요"
            disabled={isPending}
            autoComplete="current-password"
            className="min-h-12 rounded-2xl border border-input bg-background px-4 text-base text-foreground placeholder:text-muted-foreground disabled:cursor-not-allowed disabled:opacity-60"
          />
        </label>

        {errorMessage ? (
          <div className="rounded-2xl border border-destructive/20 bg-destructive/10 px-4 py-4 text-sm leading-7 text-destructive">
            {errorMessage}
          </div>
        ) : null}

        {successMessage ? (
          <div className="rounded-2xl border border-primary/20 bg-primary/10 px-4 py-4 text-sm leading-7 text-primary">
            {successMessage}
          </div>
        ) : null}

        <div className="rounded-2xl bg-secondary/70 px-4 py-4 text-sm leading-7 text-muted-foreground">
          성공 시 `localStorage`에 세션을 저장하고, `next` query 또는 기본 `/admin`
          화면으로 이동합니다.
        </div>

        <button
          type="submit"
          disabled={isPending}
          className="inline-flex min-h-12 items-center justify-center rounded-full bg-primary px-6 text-sm font-medium text-primary-foreground transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isPending ? "로그인 처리 중..." : "관리자 로그인"}
        </button>
      </form>
    </>
  );
}
