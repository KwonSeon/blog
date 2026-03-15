import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
import { setAdminSessionCookie } from "@/src/shared/lib/auth/admin-session.server";

interface AdminLoginApiResponse {
  accessToken?: string;
  expiresAt?: string;
  message?: string;
}

export async function POST(request: Request) {
  const contentType = request.headers.get("content-type");
  const body = await request.text();

  try {
    const upstreamResponse = await fetch(
      `${serverRuntimeConfig.blogApiBaseUrl}/admin/auth/login`,
      {
        method: "POST",
        cache: "no-store",
        headers: {
          Accept: "application/json",
          ...(contentType ? { "Content-Type": contentType } : {}),
        },
        body,
      },
    );

    const payload =
      ((await upstreamResponse.json().catch(() => null)) as AdminLoginApiResponse | null) ??
      null;

    if (!upstreamResponse.ok) {
      return Response.json(
        {
          message: payload?.message ?? "관리자 로그인에 실패했습니다.",
        },
        {
          status: upstreamResponse.status,
        },
      );
    }

    if (!payload?.accessToken || !payload.expiresAt) {
      return Response.json(
        {
          message: "관리자 로그인 응답이 올바르지 않습니다.",
        },
        {
          status: 502,
        },
      );
    }

    await setAdminSessionCookie(payload.accessToken, payload.expiresAt);

    return Response.json(
      {
        expiresAt: payload.expiresAt,
      },
      {
        status: upstreamResponse.status,
      },
    );
  } catch {
    return Response.json(
      {
        message: "관리자 로그인 프록시 요청 중 오류가 발생했습니다.",
      },
      {
        status: 502,
      },
    );
  }
}
