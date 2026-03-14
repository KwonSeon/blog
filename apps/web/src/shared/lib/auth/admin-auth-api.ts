import { runtimeConfig } from "@/src/shared/config/runtime";
import type { AdminSession } from "@/src/shared/lib/auth/admin-session";

interface AdminLoginPayload {
  username: string;
  password: string;
}

interface ErrorResponse {
  message?: string;
}

function toApiUrl(path: string) {
  return `${runtimeConfig.apiBaseUrl.replace(/\/$/, "")}${path}`;
}

export async function requestAdminLogin(
  payload: AdminLoginPayload,
): Promise<AdminSession> {
  const response = await fetch(toApiUrl("/admin/auth/login"), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    cache: "no-store",
    body: JSON.stringify(payload),
  });

  const data = (await response.json().catch(() => null)) as
    | (AdminSession & ErrorResponse)
    | null;

  if (!response.ok) {
    throw new Error(data?.message ?? "관리자 로그인에 실패했습니다.");
  }

  if (!data?.accessToken || !data.expiresAt) {
    throw new Error("관리자 로그인 응답이 올바르지 않습니다.");
  }

  return {
    accessToken: data.accessToken,
    expiresAt: data.expiresAt,
  };
}
