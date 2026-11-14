import { cookies } from "next/headers";
import { clearTokens, sameOrigin } from "@/src/shared/lib/auth/session";
import { revokeSsoToken } from "@/src/shared/lib/auth/sso";
export async function POST(request: Request) {
  if (!sameOrigin(request)) return new Response(null, { status: 403 });
  const refresh = (await cookies()).get("blog.refresh")?.value;
  try {
    if (refresh) await revokeSsoToken(refresh);
  } catch {
    return Response.json(
      { message: "통합 로그아웃에 실패했습니다. 다시 시도해 주세요." },
      { status: 502 },
    );
  }
  await clearTokens();
  return Response.json({ ok: true });
}
