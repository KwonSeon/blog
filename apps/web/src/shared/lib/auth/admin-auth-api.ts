interface AdminLoginPayload {
  username: string;
  password: string;
}

interface ErrorResponse {
  message?: string;
  expiresAt?: string;
}

export async function requestAdminLogin(
  payload: AdminLoginPayload,
): Promise<{ expiresAt: string }> {
  const response = await fetch("/admin/api/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    cache: "no-store",
    body: JSON.stringify(payload),
  });

  const data = (await response.json().catch(() => null)) as ErrorResponse | null;

  if (!response.ok) {
    throw new Error(data?.message ?? "관리자 로그인에 실패했습니다.");
  }

  if (!data?.expiresAt) {
    throw new Error("관리자 로그인 응답이 올바르지 않습니다.");
  }

  return {
    expiresAt: data.expiresAt,
  };
}
