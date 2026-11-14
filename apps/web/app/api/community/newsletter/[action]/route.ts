import { createHmac } from "node:crypto";
import { sameOrigin } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
type Context = { params: Promise<{ action: string }> };
export async function GET(_request: Request, context: Context) {
  const { action } = await context.params;
  if (action !== "status") return new Response(null, { status: 404 });
  try { const r = await fetch(`${serverRuntimeConfig.blogApiBaseUrl}/public/newsletter/status`, { cache: "no-store", signal: AbortSignal.timeout(10000) }); return new Response(await r.text(), { status: r.status, headers: { "Content-Type": "application/json", "Cache-Control": "no-store" } }); }
  catch { return Response.json({ enabled: false }); }
}
export async function POST(request: Request, context: Context) {
  if (!sameOrigin(request)) return new Response(null, { status: 403 });
  const { action } = await context.params;
  if (!["subscribe", "confirm", "unsubscribe"].includes(action)) return new Response(null, { status: 404 });
  const secret = process.env.BLOG_NEWSLETTER_SECRET || "";
  if (secret.length < 32) return new Response(null, { status: 503 });
  try {
    const raw = await request.text(); if (raw.length > 1024) return new Response(null, { status: 413 });
    let parsed; try { parsed = JSON.parse(raw); } catch { return new Response(null, { status: 400 }); }
    if (!parsed || typeof parsed !== "object") return new Response(null, { status: 400 });
    if (action === "subscribe" && (typeof parsed.email !== "string" || parsed.email.length > 254 || parsed.consent !== true)) return new Response(null, { status: 400 });
    if (action !== "subscribe" && (typeof parsed.token !== "string" || !/^[A-Za-z0-9_-]{43}$/.test(parsed.token))) return new Response(null, { status: 400 });
    const data = JSON.stringify(action === "subscribe" ? { email: parsed.email, consent: true } : { token: parsed.token });
    const timestamp = Math.floor(Date.now() / 1000), day = new Date(timestamp * 1000).toISOString().slice(0, 10);
    const address = request.headers.get("cf-connecting-ip") || "unknown";
    const client = createHmac("sha256", secret).update(`client:${day}:${address}`).digest("hex");
    const signature = createHmac("sha256", secret).update(`${action}\n${timestamp}\n${client}\n${data}`).digest("hex");
    const r = await fetch(`${serverRuntimeConfig.blogApiBaseUrl}/public/newsletter/${action}`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ data, client, timestamp, signature }), cache: "no-store", signal: AbortSignal.timeout(15000) });
    return new Response(r.ok ? await r.text() : null, { status: r.status, headers: { "Content-Type": "application/json", "Cache-Control": "no-store" } });
  } catch { return new Response(null, { status: 503 }); }
}
