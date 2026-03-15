import { proxyAdminApiRequest } from "@/src/shared/lib/server/admin-api-proxy";

export async function GET(request: Request) {
  return proxyAdminApiRequest({
    request,
    target: "blog",
    path: "/admin/posts",
  });
}

export async function POST(request: Request) {
  return proxyAdminApiRequest({
    request,
    target: "blog",
    path: "/admin/posts",
  });
}
