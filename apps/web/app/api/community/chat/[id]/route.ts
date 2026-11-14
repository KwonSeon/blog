import {proxyChat} from "@/src/shared/lib/chat/proxy";
export async function DELETE(request:Request,context:{params:Promise<{id:string}>}){return proxyChat(request,(await context.params).id);}
