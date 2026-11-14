import { activeToken, sameOrigin } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
type Context = { params: Promise<{ kind: string; id: string }> };
async function proxy(request: Request, context: Context, write: boolean) {
  if (write && !sameOrigin(request)) return new Response(null, { status: 403 });
  const { kind, id } = await context.params;
  if (!["posts", "projects"].includes(kind) || !/^[1-9][0-9]{0,18}$/.test(id))
    return new Response(null, { status: 404 });
  try {
    const token = await activeToken(true);
    if (write && !token) return new Response(null, { status: 401 });
    let body: string | undefined;
    if (write) {
      const text = await request.text();
      if (text.length > 100) return new Response(null, { status: 413 });
      let parsed;
      try { parsed = JSON.parse(text); } catch { return new Response(null, { status: 400 }); }
      if (!parsed || typeof parsed.liked !== "boolean") return new Response(null, { status: 400 });
      body = JSON.stringify({ liked: parsed.liked });
    }
    const response = await fetch(`${serverRuntimeConfig.blogApiBaseUrl}/${write ? "" : "public/"}reactions/${kind}/${id}`, {
      method: write ? "PUT" : "GET", headers: { "Content-Type": "application/json", ...(token ? { Authorization: `Bearer ${token}` } : {}) },
      body, cache: "no-store", signal: AbortSignal.timeout(10000),
    });
    return new Response(response.ok ? await response.text() : null, { status: response.status,
      headers: { "Content-Type": "application/json", "Cache-Control": "private, no-store" } });
  } catch { return new Response(null, { status: 503, headers: { "Cache-Control": "no-store" } }); }
}
export const GET = (request: Request, context: Context) => proxy(request, context, false);
export const PUT = (request: Request, context: Context) => proxy(request, context, true);
