import { proxyAdminApiRequest } from "@/src/shared/lib/server/admin-api-proxy";
export async function GET(
  request: Request,
  { params }: { params: Promise<{ projectId: string }> },
) {
  const { projectId } = await params;
  if (!/^\d+$/.test(projectId)) return new Response(null, { status: 400 });
  return proxyAdminApiRequest({
    request,
    target: "blog",
    path: `/admin/projects/${projectId}`,
  });
}
export async function PUT(
  request: Request,
  { params }: { params: Promise<{ projectId: string }> },
) {
  const { projectId } = await params;
  if (!/^\d+$/.test(projectId)) return new Response(null, { status: 400 });
  return proxyAdminApiRequest({
    request,
    target: "blog",
    path: `/admin/projects/${projectId}`,
  });
}
