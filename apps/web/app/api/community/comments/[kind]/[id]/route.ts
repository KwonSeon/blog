import { proxyComments } from "@/src/shared/lib/comments/proxy";
type Context = { params: Promise<{ kind: string; id: string }> };
export const GET = async (request: Request, context: Context) => proxyComments(request, await context.params);
export const POST = GET;
