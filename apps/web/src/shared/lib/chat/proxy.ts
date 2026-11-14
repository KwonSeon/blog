import {activeToken,sameOrigin} from "@/src/shared/lib/auth/session";
import {serverRuntimeConfig} from "@/src/shared/config/server-runtime";
export async function proxyChat(request:Request,id?:string){
  if(request.method!=="GET"&&!sameOrigin(request))return new Response(null,{status:403});
  const before=new URL(request.url).searchParams.get("before")??"0";
  if(!/^(0|[1-9][0-9]{0,18})$/.test(before)||(id&&!/^[1-9][0-9]{0,18}$/.test(id)))return new Response(null,{status:400});
  try{
    const token=await activeToken(true);if(!token)return new Response(null,{status:401});
    let body:string|undefined;
    if(request.method==="POST"){
      const text=await request.text();if(text.length>12000)return new Response(null,{status:413});
      let input;try{input=JSON.parse(text);}catch{return new Response(null,{status:400});}
      if(!input||typeof input.body!=="string"||!input.body.trim()||input.body.length>2000||typeof input.requestKey!=="string"||input.requestKey.length!==36)return new Response(null,{status:400});
      body=JSON.stringify({body:input.body,requestKey:input.requestKey});
    }
    const response=await fetch(`${serverRuntimeConfig.blogApiBaseUrl}/chat/messages${id?`/${id}`:`?before=${before}`}`,{method:request.method,headers:{Authorization:`Bearer ${token}`,"Content-Type":"application/json"},body,cache:"no-store",signal:AbortSignal.timeout(10000)});
    return new Response(response.ok?await response.text():null,{status:response.status,headers:{"Content-Type":"application/json","Cache-Control":"private, no-store",...(response.status===429?{"Retry-After":"60"}:{})}});
  }catch{return new Response(null,{status:503,headers:{"Cache-Control":"private, no-store"}});}
}
