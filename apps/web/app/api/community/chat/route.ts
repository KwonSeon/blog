import {proxyChat} from "@/src/shared/lib/chat/proxy";
export const GET=(request:Request)=>proxyChat(request);
export const POST=(request:Request)=>proxyChat(request);
