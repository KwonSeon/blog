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

  return proxyAdminApiRequest({
    request,
    target: "media",
    path: `/admin/media/assets/${mediaAssetId}`,
  });
}
