import { proxyAdminApiRequest } from "@/src/shared/lib/server/admin-api-proxy";
async function proxy(request: Request) {
  return proxyAdminApiRequest({
    request,
    target: "blog",
    path: "/admin/site-settings",
  });
}
export { proxy as GET, proxy as PUT };
