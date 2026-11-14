import { proxyNotifications } from "@/src/shared/lib/notifications";
export async function POST(request: Request) { return proxyNotifications(request); }
