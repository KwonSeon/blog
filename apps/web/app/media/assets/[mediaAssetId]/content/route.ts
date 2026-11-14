import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
export async function GET(
  _request: Request,
  { params }: { params: Promise<{ mediaAssetId: string }> },
) {
  const { mediaAssetId } = await params;
  if (!/^\d+$/.test(mediaAssetId)) return new Response(null, { status: 400 });
  const response = await fetch(
    `${serverRuntimeConfig.mediaApiBaseUrl}/public/media/assets/${mediaAssetId}/content`,
    { signal: AbortSignal.timeout(15000) },
  );
  const headers = new Headers({ "X-Content-Type-Options": "nosniff" });
  for (const name of ["content-type", "cache-control", "etag"]) {
    const value = response.headers.get(name);
    if (value) headers.set(name, value);
  }
  return new Response(response.body, { status: response.status, headers });
}
