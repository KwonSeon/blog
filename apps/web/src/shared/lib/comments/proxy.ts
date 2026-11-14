import { activeToken, sameOrigin } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
export async function proxyComments(request: Request, params: { kind: string; id: string; comment?: string }) {
  const write = request.method !== "GET";
  if (write && !sameOrigin(request)) return new Response(null, { status: 403 });
  const { kind, id, comment } = params;
  const validId = (v: string) => /^[1-9][0-9]{0,18}$/.test(v);
  if (!["posts", "projects"].includes(kind) || !validId(id) || (comment && !validId(comment))) return new Response(null, { status: 404 });
  const after = new URL(request.url).searchParams.get("after") || "0";
  if (!/^(0|[1-9][0-9]{0,18})$/.test(after)) return new Response(null, { status: 400 });
  try {
    const token = await activeToken(true);
    if (write && !token) return new Response(null, { status: 401 });
    let body: string | undefined;
    if (["POST", "PATCH"].includes(request.method)) {
      const text = await request.text();
      if (text.length > 12000) return new Response(null, { status: 413 });
      let value;
      try { value = JSON.parse(text); } catch { return new Response(null, { status: 400 }); }
      if (!value || typeof value.body !== "string" || !value.body.trim() || value.body.length > 2000) return new Response(null, { status: 400 });
      if (request.method === "POST" && (typeof value.requestKey !== "string" || (value.parentId != null && (typeof value.parentId !== "string" || !validId(value.parentId))))) return new Response(null, { status: 400 });
      body = JSON.stringify({ body: value.body, ...(request.method === "POST" ? { parentId: value.parentId ?? null, requestKey: value.requestKey } : {}) });
    }
    const path = `${write ? "" : "public/"}comments/${kind}/${id}${comment ? `/${comment}` : ""}${write ? "" : `?after=${after}`}`;
    const response = await fetch(`${serverRuntimeConfig.blogApiBaseUrl}/${path}`, {
      method: request.method, headers: { "Content-Type": "application/json", ...(token ? { Authorization: `Bearer ${token}` } : {}) },
      body, cache: "no-store", signal: AbortSignal.timeout(10000),
    });
    return new Response(response.ok ? await response.text() : null, { status: response.status,
      headers: { "Content-Type": "application/json", "Cache-Control": "private, no-store", ...(response.status === 429 ? { "Retry-After": "60" } : {}) } });
  } catch { return new Response(null, { status: 503, headers: { "Cache-Control": "no-store" } }); }
}
