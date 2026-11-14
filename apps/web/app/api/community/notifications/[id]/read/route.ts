import { proxyNotifications } from "@/src/shared/lib/notifications";
export async function PATCH(request: Request, context: { params: Promise<{ id: string }> }) { return proxyNotifications(request, (await context.params).id); }
