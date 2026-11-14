import { proxyAdminApiRequest } from "@/src/shared/lib/server/admin-api-proxy";
async function proxy(request: Request) {
  return proxyAdminApiRequest({request, target: "blog", path: "/admin/notifications/telegram"});
}
export {proxy as GET, proxy as PUT};
