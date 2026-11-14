import {
  activeToken,
  clearTokens,
  sameOrigin,
} from "@/src/shared/lib/auth/session";
import { ssoSettings } from "@/src/shared/lib/auth/sso";
export async function POST(request: Request) {
  if (!sameOrigin(request)) return new Response(null, { status: 403 });
  const token = await activeToken(true);
  if (!token) return new Response(null, { status: 401 });
  const response = await fetch(
    `${ssoSettings().issuer.origin}/api/auth/logout-all`,
    {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      signal: AbortSignal.timeout(10000),
    },
  );
  if (!response.ok) return new Response(null, { status: 502 });
  await clearTokens();
  return Response.json({ ok: true });
}
