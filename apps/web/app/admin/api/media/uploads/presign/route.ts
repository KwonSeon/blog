import { proxyAdminApiRequest } from "@/src/shared/lib/server/admin-api-proxy";

export async function POST(request: Request) {
  return proxyAdminApiRequest({
    request,
    target: "media",
    path: "/admin/media/uploads/presign",
  });
}
