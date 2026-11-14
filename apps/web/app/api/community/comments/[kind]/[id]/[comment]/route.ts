import { proxyComments } from "@/src/shared/lib/comments/proxy";
type Context = { params: Promise<{ kind: string; id: string; comment: string }> };
export const PATCH = async (request: Request, context: Context) => proxyComments(request, await context.params);
export const DELETE = PATCH;
