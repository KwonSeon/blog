"use client";
import {useEffect,useState} from "react";
import {Button} from "@astryxdesign/core/Button";
import {CheckboxInput} from "@astryxdesign/core/CheckboxInput";
import {Skeleton} from "@astryxdesign/core/Skeleton";
type Status={configured:boolean;active:boolean;currentConfiguration:boolean;recipientCount:number;states:Record<string,number>};
const states: Record<string,string> = {QUEUED:"발송 대기",SENDING:"발송 중",SENT:"발송 완료",FAILED:"발송 실패",UNKNOWN:"결과 확인 필요",SKIPPED:"발송 제외"};
export function DeliveryPanel(){
  const [status,setStatus]=useState<Status|null>(null);
  const [busy,setBusy]=useState(true),[error,setError]=useState(""),[confirmed,setConfirmed]=useState(false);
  async function request(enabled?:boolean){
    setBusy(true);setError("");
    try{
      const response=await fetch("/admin/api/notifications/telegram",{method:enabled===undefined?"GET":"PUT",cache:"no-store",headers:{"Content-Type":"application/json"},body:enabled===undefined?undefined:JSON.stringify({enabled,confirmed}),signal:AbortSignal.timeout(15000)});
      if(!response.ok)throw new Error("알림 설정을 처리하지 못했습니다. 로그인 상태와 서버 설정을 확인한 뒤 다시 시도해 주세요.");
      const value:Status=await response.json();setStatus(value);setConfirmed(false);
    }catch{setError("알림 설정을 처리하지 못했습니다. 로그인 상태와 서버 설정을 확인한 뒤 다시 시도해 주세요.");}
    finally{setBusy(false);}
  }
  useEffect(()=>{
    const controller=new AbortController();
    fetch("/admin/api/notifications/telegram",{cache:"no-store",signal:AbortSignal.any([controller.signal,AbortSignal.timeout(15000)])})
      .then(response=>{if(!response.ok)throw new Error();return response.json() as Promise<Status>;})
      .then(value=>{if(!controller.signal.aborted)setStatus(value);})
      .catch(()=>{if(!controller.signal.aborted)setError("알림 설정을 불러오지 못했습니다. 다시 시도해 주세요.");})
      .finally(()=>{if(!controller.signal.aborted)setBusy(false);});
    return()=>controller.abort();
  },[]);
  return <section aria-label="Telegram 알림 설정" className="space-y-6 rounded-2xl border border-border p-6">
    <h2 className="text-xl font-semibold">Telegram</h2>
    {!status&&busy&&<div role="status" aria-label="설정을 불러오는 중입니다"><Skeleton width="80%" height={24}/><Skeleton width="60%" height={24}/></div>}
    {error&&<p role="alert">{error}</p>}
    {status&&<><dl className="grid grid-cols-2 gap-4"><dt>연결 준비</dt><dd>{status.configured?"준비됨":"미설정"}</dd><dt>발송 상태</dt><dd>{!status.active?"꺼짐":status.currentConfiguration?"켜짐":"설정 변경 확인 필요"}</dd><dt>등록된 수신 계정</dt><dd>{status.recipientCount}개</dd></dl>
      {!status.configured&&<p>봇 연결과 수신 계정 등록이 필요합니다. 준비가 끝날 때까지 발송은 시작되지 않습니다.</p>}
      {status.configured&&!status.currentConfiguration&&<CheckboxInput label="등록된 수신 계정의 동의와 연결 정보를 확인했습니다" value={confirmed} onChange={setConfirmed} isDisabled={busy}/>}
      <div className="flex flex-wrap gap-3">
        {status.configured&&!status.currentConfiguration&&<Button label="발송 시작" variant="primary" isDisabled={!confirmed||busy} onClick={()=>void request(true)}/>}
        {status.active&&<Button label="발송 중지" isDisabled={busy} onClick={()=>void request(false)}/>}
      </div>
      <dl className="grid grid-cols-2 gap-3">{Object.entries(states).map(([key,label])=><div key={key} className="space-y-1"><dt className="text-sm text-muted-foreground">{label}</dt><dd>{status.states[key]??0}건</dd></div>)}</dl>
      <p className="text-sm leading-6 text-muted-foreground">결과가 불명확한 메시지는 중복 발송을 막기 위해 자동으로 재발송하지 않습니다. 발송 중지는 이미 전달된 메시지를 취소하지 않습니다.</p>
    </>}
    <Button label={error?"다시 시도":"새로고침"} isLoading={busy} onClick={()=>void request()}/>
  </section>;
}
