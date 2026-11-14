import { proxyAdminApiRequest } from "@/src/shared/lib/server/admin-api-proxy";
async function proxy(
  request: Request,
  { params }: { params: Promise<{ projectId: string }> },
) {
  const { projectId } = await params;
  if (!/^\d+$/.test(projectId)) return new Response(null, { status: 400 });
  return proxyAdminApiRequest({
    request,
    target: "blog",
    path: `/admin/projects/${projectId}/story`,
  });
}
export { proxy as GET, proxy as PUT };
