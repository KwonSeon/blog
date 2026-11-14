import { proxyNotifications } from "@/src/shared/lib/notifications";
export async function GET(request: Request) { return proxyNotifications(request); }
