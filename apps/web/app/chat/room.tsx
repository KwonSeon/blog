"use client";
import {useEffect,useRef,useState} from "react";
import {Button} from "@astryxdesign/core/Button";
import {Skeleton} from "@astryxdesign/core/Skeleton";
import {FormTextarea} from "@/src/shared/ui/form-textarea";
import {connectChat} from "@/src/shared/lib/chat/socket";
type Item={id:string;author:string;body:string;createdAt:string;deleted:boolean;canDelete:boolean};
type Page={items:Item[];olderCursor:string|null;enabled:boolean};
export function MemberChat(){
 const [page,setPage]=useState<Page|null>(null),[before,setBefore]=useState("0"),[revision,setRevision]=useState(0),[live,setLive]=useState(false);
 const [loading,setLoading]=useState(true),[sending,setSending]=useState(false),[deleting,setDeleting]=useState<string|null>(null),[error,setError]=useState(""),[writeError,setWriteError]=useState(""),[body,setBody]=useState("");
 const attempt=useRef<{body:string;key:string}|null>(null);const bottom=useRef<HTMLDivElement>(null);const [nearBottom,setNearBottom]=useState(true);
 useEffect(()=>{
   const abort=new AbortController();
   fetch(`/api/community/chat?before=${before}`,{cache:"no-store",signal:AbortSignal.any([abort.signal,AbortSignal.timeout(15000)])})
    .then(async response=>{if(!response.ok)throw new Error(response.status===401?"로그인이 만료됐습니다. 다시 로그인해 주세요.":"대화를 불러오지 못했습니다. 다시 시도해 주세요.");return await response.json() as Page;})
    .then(value=>{if(!abort.signal.aborted){setPage(value);setError("");}})
    .catch(reason=>{if(!abort.signal.aborted)setError(reason instanceof Error?reason.message:"대화를 불러오지 못했습니다.");})
    .finally(()=>{if(!abort.signal.aborted)setLoading(false);});
   return()=>abort.abort();
 },[before,revision]);
 useEffect(()=>{const timer=setInterval(()=>{if(!document.hidden)setRevision(v=>v+1);},15000);const visible=()=>{if(!document.hidden)setRevision(v=>v+1);};document.addEventListener("visibilitychange",visible);return()=>{clearInterval(timer);document.removeEventListener("visibilitychange",visible);};},[]);
 useEffect(()=>{if(!page?.enabled)return;let timer:ReturnType<typeof setTimeout>|undefined;const close=connectChat(()=>{if(timer)clearTimeout(timer);timer=setTimeout(()=>setRevision(v=>v+1),200);},setLive);return()=>{close();if(timer)clearTimeout(timer);};},[page?.enabled]);
 const last=page?.items.at(-1)?.id;
 useEffect(()=>{if(before==="0"&&nearBottom)bottom.current?.scrollIntoView({block:"nearest"});},[last,before,nearBottom]);
 async function send(event:React.FormEvent){
  event.preventDefault();if(sending||!body.trim())return;setSending(true);setWriteError("");
  if(!attempt.current||attempt.current.body!==body)attempt.current={body,key:crypto.randomUUID()};
  try{
   const response=await fetch("/api/community/chat",{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify({body:attempt.current.body,requestKey:attempt.current.key}),signal:AbortSignal.timeout(15000)});
   if(!response.ok)throw new Error(response.status===429?"메시지를 너무 빠르게 보냈습니다. 잠시 후 다시 보내주세요.":response.status===401?"로그인이 만료됐습니다. 다시 로그인해 주세요.":"보내지 못했습니다. 같은 내용으로 다시 시도하면 중복 전송을 방지합니다.");
   attempt.current=null;setBody("");setBefore("0");setNearBottom(true);setRevision(v=>v+1);
  }catch(reason){setWriteError(reason instanceof Error?reason.message:"보내지 못했습니다.");}finally{setSending(false);}
 }
 async function remove(id:string){
  setDeleting(id);setWriteError("");try{const response=await fetch(`/api/community/chat/${id}`,{method:"DELETE",signal:AbortSignal.timeout(15000)});if(!response.ok)throw new Error();setRevision(v=>v+1);}catch{setWriteError("메시지를 삭제하지 못했습니다. 다시 시도해 주세요.");}finally{setDeleting(null);}
 }
 function view(cursor:string){setLoading(true);setPage(null);setBefore(cursor);setRevision(v=>v+1);}
 return <section aria-label="자유 대화" className="space-y-5">
  <div className="flex flex-wrap items-center justify-between gap-3"><h2 className="text-xl font-semibold">자유 대화</h2><p className="text-sm text-muted-foreground">{before!=="0"?"이전 대화 확인 중":live?"실시간 연결됨":"주기적으로 새 대화 확인 중"}</p></div>
  {error&&<p role="alert">{error}</p>}
  {!page&&loading&&<div role="status" aria-label="대화를 불러오는 중입니다" className="space-y-4"><Skeleton width="70%" height={54}/><Skeleton width="90%" height={54}/><Skeleton width="60%" height={54}/></div>}
  {page&&!page.enabled&&<p>채팅방을 준비하고 있습니다.</p>}
  {page?.enabled&&<>
   <div className="flex flex-wrap gap-3">{page.olderCursor&&<Button label="이전 대화" onClick={()=>view(page.olderCursor!)} isDisabled={loading}/>} {before!=="0"&&<Button label="최근 대화" onClick={()=>view("0")} isDisabled={loading}/>}</div>
   <div className="max-h-[55vh] overflow-y-auto rounded-2xl border border-border p-4" tabIndex={0} aria-label="대화 이력" onScroll={e=>{const target=e.currentTarget;setNearBottom(target.scrollHeight-target.scrollTop-target.clientHeight<80);}}>
    {page.items.length===0?<p className="py-8 text-center text-muted-foreground">아직 대화가 없습니다. 첫 인사를 남겨보세요.</p>:<ol className="space-y-5">{page.items.map(item=><li key={item.id} data-message-id={item.id} className="space-y-2 break-words"><div className="flex flex-wrap items-center gap-3"><strong>{item.author}</strong><time dateTime={item.createdAt} className="text-xs text-muted-foreground">{new Date(item.createdAt).toLocaleString("ko-KR")}</time>{item.canDelete&&<Button label="삭제" size="sm" isDisabled={deleting!==null} isLoading={deleting===item.id} onClick={()=>void remove(item.id)}/>}</div><p className="whitespace-pre-wrap [overflow-wrap:anywhere]">{item.deleted?"삭제된 메시지입니다.":item.body}</p></li>)}</ol>}
    <div ref={bottom}/>
   </div>
   <form onSubmit={send} className="space-y-3"><FormTextarea label="메시지" value={body} onChange={e=>setBody(e.target.value)} rows={3} maxLength={2000} disabled={sending}/><div className="flex justify-end"><Button label="보내기" type="submit" variant="primary" isLoading={sending} isDisabled={!body.trim()}/></div></form>
  </>}
  {writeError&&<p role="alert">{writeError}</p>}
  {error&&<Button label="다시 시도" onClick={()=>{setLoading(true);setRevision(v=>v+1);}}/>}
 </section>;
}
