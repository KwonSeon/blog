import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
export const dynamic = "force-dynamic";
export async function GET() {
  try {
    const response = await fetch(`${serverRuntimeConfig.blogApiBaseUrl}/health`, { cache: "no-store", signal: AbortSignal.timeout(5000) });
    if (!response.ok || !(await response.json())) throw new Error("Unhealthy API");
    return Response.json({status: "UP"}, {headers: {"Cache-Control": "no-store"}});
  } catch { return Response.json({status: "DOWN"}, {status: 503}); }
}
