import "server-only";
import { activeToken, sameOrigin } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";

export async function proxyNotifications(request: Request, id?: string) {
  const write = request.method !== "GET";
  if (write && !sameOrigin(request)) return new Response(null, { status: 403 });
  if (id && !/^[1-9][0-9]{0,18}$/.test(id)) return new Response(null, { status: 400 });
  const before = new URL(request.url).searchParams.get("before") || "0";
  if (!/^(0|[1-9][0-9]{0,18})$/.test(before)) return new Response(null, { status: 400 });
  try {
    const token = await activeToken(true);
    if (!token) return new Response(null, { status: 401 });
    let body: string | undefined;
    if (request.method === "POST") {
      const raw = await request.text();
      if (raw.length > 100) return new Response(null, { status: 413 });
      const input = JSON.parse(raw);
      if (typeof input?.through !== "string" || !/^(0|[1-9][0-9]{0,18})$/.test(input.through)) return new Response(null, { status: 400 });
      body = JSON.stringify({ through: input.through });
    }
    const path = id ? `/${id}/read` : request.method === "POST" ? "/read-all" : `?before=${before}`;
    const response = await fetch(`${serverRuntimeConfig.blogApiBaseUrl}/notifications${path}`, {
      method: request.method, headers: { Authorization: `Bearer ${token}`, "Content-Type": "application/json" },
      body, cache: "no-store", signal: AbortSignal.timeout(10000),
    });
    return new Response(response.ok && response.status !== 204 ? await response.text() : null, {
      status: response.status, headers: { "Cache-Control": "private, no-store", ...(response.status === 204 ? {} : { "Content-Type": "application/json" }) },
    });
  } catch (error) {
    return new Response(null, { status: error instanceof SyntaxError ? 400 : 503, headers: { "Cache-Control": "no-store" } });
  }
}
