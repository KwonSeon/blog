"use client";
import { useEffect, useRef, useState } from "react";
import { Button } from "@astryxdesign/core/Button";
import { TextInput } from "@astryxdesign/core/TextInput";
import { CheckboxInput } from "@astryxdesign/core/CheckboxInput";
export function NewsletterForm() {
  const [ready, setReady] = useState<boolean | null>(null), [email, setEmail] = useState(""), [consent, setConsent] = useState(false), [busy, setBusy] = useState(false), [message, setMessage] = useState(""); const lock = useRef(false);
  useEffect(() => { const c = new AbortController(); fetch("/api/community/newsletter/status", { cache: "no-store", signal: c.signal }).then(r => r.json()).then(d => setReady(d.enabled)).catch(() => { if (!c.signal.aborted) setReady(false); }); return () => c.abort(); }, []);
  async function send() { if (lock.current || !consent || !ready) return; lock.current = true; setBusy(true); setMessage(""); try { const r = await fetch("/api/community/newsletter/subscribe", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ email, consent }) }); if (!r.ok) throw new Error(); setMessage("신청 가능한 주소라면 확인 메일을 보내드립니다. 받은 편지함과 스팸함을 확인하고, 메일의 링크에서 구독을 완료해 주세요."); } catch { setMessage("신청하지 못했습니다. 이메일 주소를 확인하고 잠시 후 다시 시도해 주세요."); } finally { lock.current = false; setBusy(false); } }
  return <form onSubmit={e => { e.preventDefault(); void send(); }} className="space-y-5">
    <TextInput label="이메일 주소" type="email" value={email} onChange={setEmail} isRequired isDisabled={busy || ready !== true} placeholder="name@example.com" />
    <CheckboxInput label="새 글 알림을 이메일로 받는 데 동의합니다." value={consent} onChange={setConsent} isDisabled={busy || ready !== true} />
    <p className="text-sm leading-6 text-muted-foreground">이메일에서 확인한 뒤 구독이 활성화됩니다. 알림 메일의 해지 링크로 언제든 중단할 수 있습니다. <a className="underline" href="/privacy">개인정보 처리 안내</a></p>
    <Button type="submit" label="확인 메일 받기" size="lg" isLoading={busy} isDisabled={busy || !consent || !email || ready !== true} />
    {ready === false && <p role="status">이메일 구독을 준비 중입니다. 잠시 후 다시 방문해 주세요.</p>}{message && <p role="status" className="text-sm leading-6">{message}</p>}
  </form>;
}
