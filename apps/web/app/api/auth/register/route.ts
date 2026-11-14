import { sameOrigin } from "@/src/shared/lib/auth/session";
import { ssoSettings } from "@/src/shared/lib/auth/sso";
export async function POST(request: Request) {
  if (!sameOrigin(request)) return new Response(null, { status: 403 });
  const raw = await request.text();
  if (raw.length > 8192) return new Response(null, { status: 413 });
  let input;
  try {
    input = JSON.parse(raw);
  } catch {
    return new Response(null, { status: 400 });
  }
  const paths: Record<string, string> = {
    request: "/email-verifications",
    confirm: "/email-verifications/confirm",
    register: "/register",
  };
  if (!paths[input.action]) return new Response(null, { status: 400 });
  const { action, ...body } = input;
  try {
    const response = await fetch(
      `${ssoSettings().issuer.origin}/api/auth${paths[action]}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
        signal: AbortSignal.timeout(10000),
        cache: "no-store",
      },
    );
    const data = await response.json().catch(() => null);
    if (!response.ok)
      return Response.json(
        {
          message:
            "요청을 처리하지 못했습니다. 입력 내용과 인증번호를 확인해 주세요.",
        },
        { status: response.status },
      );
    // Registration may issue legacy tokens. Never return those to the browser: sign in through OIDC.
    return Response.json(action === "register" ? { ok: true } : data, {
      headers: { "Cache-Control": "no-store" },
    });
  } catch {
    return Response.json(
      { message: "계정 서비스에 연결하지 못했습니다." },
      { status: 502 },
    );
  }
}
