"use client";
import { useEffect, useRef, useState } from "react";
import { ToggleButton } from "@astryxdesign/core/ToggleButton";
import { Button } from "@astryxdesign/core/Button";
type State = { count: number; liked: boolean; authenticated: boolean };
export function ContentLike({ kind, id, path }: { kind: "posts" | "projects"; id: string; path: string }) {
  const [state, setState] = useState<State | null>(null);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const pending = useRef(false);
  const endpoint = `/api/community/reactions/${kind}/${id}`;
  useEffect(() => {
    const controller = new AbortController();
    fetch(endpoint, { cache: "no-store", signal: controller.signal }).then(async response => {
      if (!response.ok) throw new Error();
      setState(await response.json());
    }).catch(() => { if (!controller.signal.aborted) setError("좋아요를 불러오지 못했습니다. 새로고침해 주세요."); });
    return () => controller.abort();
  }, [endpoint]);
  async function change(liked: boolean) {
    if (pending.current) return;
    pending.current = true; setBusy(true); setError("");
    try {
      const response = await fetch(endpoint, { method: "PUT", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ liked }) });
      if (response.status === 401) { if (state) setState({ ...state, authenticated: false }); setError("다시 로그인해 주세요."); return; }
      if (!response.ok) throw new Error();
      setState(await response.json());
    } catch { setError("좋아요를 저장하지 못했습니다. 다시 눌러 주세요."); }
    finally { pending.current = false; setBusy(false); }
  }
  return <section aria-label="콘텐츠 좋아요" className="my-8 flex flex-wrap items-center gap-3">
    {state?.authenticated ? <ToggleButton label={state.liked ? "좋아요 취소" : "좋아요"} isPressed={state.liked} isLoading={busy} isDisabled={busy} size="lg" onPressedChange={value => void change(value)} />
      : <Button label={state ? "로그인하고 좋아요" : "좋아요 불러오는 중"} isDisabled={!state} size="lg" href={`/api/auth/sso/start?next=${encodeURIComponent(path)}`} />}
    {state && <span role="status" className="text-sm text-muted-foreground">좋아요 {state.count.toLocaleString("ko-KR")}개</span>}
    {error && <p role="alert" className="w-full text-sm text-destructive">{error}</p>}
  </section>;
}
