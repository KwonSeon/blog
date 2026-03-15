import { proxyAdminApiRequest } from "@/src/shared/lib/server/admin-api-proxy";

interface AdminPostStatusRouteProps {
  params: Promise<{
    postId: string;
  }>;
}

export async function PATCH(
  request: Request,
  { params }: AdminPostStatusRouteProps,
) {
  const { postId } = await params;

  return proxyAdminApiRequest({
    request,
    target: "blog",
    path: `/admin/posts/${postId}/status`,
  });
}
