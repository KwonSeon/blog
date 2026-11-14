import { parseApiJson } from "@/src/shared/lib/api/json";
import { decodeJwt } from "jose";
import { editorToken, sameOrigin } from "@/src/shared/lib/auth/session";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
async function authorizeAsset(request: Request, id: string) {
  if (!/^\d+$/.test(id)) return new Response(null, { status: 400 });
  if (request.method !== "GET" && !sameOrigin(request))
    return new Response(null, { status: 403 });
  const token = await editorToken(true);
  if (!token) return new Response(null, { status: 401 });
  const response = await fetch(
    `${serverRuntimeConfig.mediaApiBaseUrl}/admin/media/assets/${id}`,
    {
      headers: { Authorization: `Bearer ${token}` },
      cache: "no-store",
      signal: AbortSignal.timeout(10000),
    },
  );
  if (!response.ok) return new Response(null, { status: response.status });
  const asset = parseApiJson(await response.text()) as {
    uploadedByUserId: string;
    namespace: string;
  };
  if (
    String(asset.uploadedByUserId) !== decodeJwt(token).sub ||
    !["post-cover", "post-inline", "project-cover", "project-inline"].includes(
      asset.namespace,
    )
  )
    return new Response(null, { status: 403 });
  return null;
}
import { proxyAdminApiRequest } from "@/src/shared/lib/server/admin-api-proxy";

interface AdminMediaAssetRouteProps {
  params: Promise<{
    mediaAssetId: string;
  }>;
}

export async function GET(
  request: Request,
  { params }: AdminMediaAssetRouteProps,
) {
  const { mediaAssetId } = await params;
  const denied = await authorizeAsset(request, mediaAssetId);
  if (denied) return denied;

  return proxyAdminApiRequest({
    request,
    target: "media",
    path: `/admin/media/assets/${mediaAssetId}`,
  });
}

export async function DELETE(
  request: Request,
  { params }: AdminMediaAssetRouteProps,
) {
  const { mediaAssetId } = await params;
  const denied = await authorizeAsset(request, mediaAssetId);
  if (denied) return denied;

  return proxyAdminApiRequest({
    request,
    target: "media",
    path: `/admin/media/assets/${mediaAssetId}`,
  });
}
