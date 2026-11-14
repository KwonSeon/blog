import { createHmac, randomBytes } from "node:crypto";
import { cookies } from "next/headers";
import { sameOrigin, cookieOptions } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
type Context = { params: Promise<{ kind: string; id: string }> };
async function proxy(request: Request, context: Context, write: boolean) {
  if (write && !sameOrigin(request)) return new Response(null, { status: 403 });
  const { kind, id } = await context.params;
  if (!["posts", "projects"].includes(kind) || !/^[1-9][0-9]{0,18}$/.test(id)) return new Response(null, { status: 404 });
  try {
    const jar = await cookies();
    const existing = jar.get("blog.visitor")?.value;
    const visitor = existing && /^[a-f0-9]{64}$/.test(existing) ? existing : randomBytes(32).toString("hex");
    let body: string | undefined;
    const track = write && request.headers.get("DNT") !== "1" && request.headers.get("Sec-GPC") !== "1";
    if (track) {
      const secret = process.env.BLOG_VIEW_SIGNING_SECRET || "";
      if (secret.length < 32) return new Response(null, { status: 503 });
      const timestamp = Math.floor(Date.now() / 1000), day = new Date(timestamp * 1000).toISOString().slice(0, 10);
      const hash = createHmac("sha256", secret).update(`visitor:${day}:${visitor}`).digest("hex");
      const signature = createHmac("sha256", secret).update(`${kind}:${id}:${hash}:${timestamp}`).digest("hex");
      body = JSON.stringify({ visitor: hash, timestamp, signature });
    }
    const response = await fetch(`${serverRuntimeConfig.blogApiBaseUrl}/public/views/${kind}/${id}`, {
      method: track ? "POST" : "GET", headers: { "Content-Type": "application/json" }, body,
      cache: "no-store", signal: AbortSignal.timeout(10000),
    });
    if (track && response.ok && visitor !== existing) jar.set("blog.visitor", visitor, { ...cookieOptions, maxAge: 86400 - Math.floor(Date.now() / 1000) % 86400 });
    return new Response(response.ok ? await response.text() : null, { status: response.status,
      headers: { "Content-Type": "application/json", "Cache-Control": "private, no-store" } });
  } catch { return new Response(null, { status: 503, headers: { "Cache-Control": "no-store" } }); }
}
export const GET = (request: Request, context: Context) => proxy(request, context, false);
export const POST = (request: Request, context: Context) => proxy(request, context, true);
