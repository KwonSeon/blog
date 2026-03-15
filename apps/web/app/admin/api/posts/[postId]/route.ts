import { proxyAdminApiRequest } from "@/src/shared/lib/server/admin-api-proxy";

interface AdminPostRouteProps {
  params: Promise<{
    postId: string;
  }>;
}

export async function GET(request: Request, { params }: AdminPostRouteProps) {
  const { postId } = await params;

  return proxyAdminApiRequest({
    request,
    target: "blog",
    path: `/admin/posts/${postId}`,
  });
}

export async function PUT(request: Request, { params }: AdminPostRouteProps) {
  const { postId } = await params;

  return proxyAdminApiRequest({
    request,
    target: "blog",
    path: `/admin/posts/${postId}`,
  });
}

export async function DELETE(request: Request, { params }: AdminPostRouteProps) {
  const { postId } = await params;

  return proxyAdminApiRequest({
    request,
    target: "blog",
    path: `/admin/posts/${postId}`,
  });
}
