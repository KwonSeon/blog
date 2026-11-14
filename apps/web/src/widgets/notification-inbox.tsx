"use client";
import { useEffect, useRef, useState } from "react";
import { Button } from "@astryxdesign/core/Button";
import { Skeleton } from "@astryxdesign/core/Skeleton";
import Link from "next/link";

type Item = { id: string; kind: "COMMENT" | "REPLY"; title: string; actor: string; path: string; createdAt: string; read: boolean };
type Page = { items: Item[]; nextCursor: string | null; snapshotId: string; unreadCount: number };
const endpoint = "/api/community/notifications";
export function NotificationInbox() {
  const [page, setPage] = useState<Page | null>(null), [error, setError] = useState(""), [busy, setBusy] = useState(false), [attempt, setAttempt] = useState(0);
  const lock = useRef(false);
  useEffect(() => {
    const controller = new AbortController();
    fetch(endpoint, { cache: "no-store", signal: AbortSignal.any([controller.signal, AbortSignal.timeout(15000)]) }).then(async r => {
      if (!r.ok) throw new Error(r.status === 401 ? "로그인이 만료됐습니다. 다시 로그인해 주세요." : "알림을 불러오지 못했습니다.");
      setPage(await r.json());
    }).catch(e => { if (!controller.signal.aborted) setError(e instanceof Error ? e.message : "알림을 불러오지 못했습니다."); });
    return () => controller.abort();
  }, [attempt]);
  async function read(item?: Item) {
    if (lock.current || !page) return;
    lock.current = true; setBusy(true); setError("");
    try {
      const r = await fetch(item ? `${endpoint}/${item.id}/read` : `${endpoint}/read-all`, {
        method: item ? "PATCH" : "POST", headers: { "Content-Type": "application/json" },
        body: item ? undefined : JSON.stringify({ through: page.snapshotId }), signal: AbortSignal.timeout(15000),
      });
      if (!r.ok) throw new Error();
      const result: { unreadCount: number } = await r.json();
      setPage(current => current ? { ...current, unreadCount: result.unreadCount,
        items: current.items.map(row => !item || row.id === item.id ? { ...row, read: true } : row) } : current);
    } catch { setError("읽음 처리에 실패했습니다. 다시 시도해 주세요."); }
    finally { lock.current = false; setBusy(false); }
  }
  async function more() {
    if (lock.current || !page?.nextCursor) return;
    lock.current = true; setBusy(true); setError("");
    try {
      const r = await fetch(`${endpoint}?before=${page.nextCursor}`, { cache: "no-store", signal: AbortSignal.timeout(15000) });
      if (!r.ok) throw new Error();
      const next: Page = await r.json();
      setPage(current => ({ ...next, items: [...(current?.items || []), ...next.items] }));
    } catch { setError("이전 알림을 불러오지 못했습니다."); }
    finally { lock.current = false; setBusy(false); }
  }
  return <section aria-label="내 알림">
    <p className="mb-6 text-sm text-muted-foreground">최근 90일의 댓글 알림입니다. 비공개 또는 삭제된 콘텐츠의 알림은 표시하지 않습니다.</p>
    {error && <p role="alert" className="my-4 text-destructive">{error}</p>}
    {!page && !error && <NotificationInboxLoading />}
    {!page && error && <Button label="다시 불러오기" onClick={() => { setError(""); setAttempt(n => n + 1); }} />}
    {page && <><div className="mb-6 flex flex-wrap items-center justify-between gap-4"><p role="status">읽지 않은 알림 {page.unreadCount}개</p><Button label="새로고침" isDisabled={busy} onClick={() => { setPage(null); setError(""); setAttempt(n => n + 1); }} /><Button label="모두 읽음" isDisabled={busy || !page.unreadCount} onClick={() => void read()} /></div>
      {!page.items.length ? <p className="rounded-lg border border-border p-8 text-muted-foreground">아직 받은 알림이 없습니다.</p> : <ul className="space-y-4">{page.items.map(item => <li key={item.id} className={`rounded-lg border p-5 ${item.read ? "border-border" : "border-primary/40 bg-secondary/40"}`}>
        <div className="mb-2 flex flex-wrap gap-3 text-xs text-muted-foreground"><span>{item.read ? "읽음" : "새 알림"}</span><time dateTime={item.createdAt}>{new Date(item.createdAt).toLocaleString("ko-KR")}</time></div>
        <p className="mb-2 text-sm">{item.actor || "회원"}님이 {item.kind === "REPLY" ? "내 댓글에 답글을 남겼습니다." : "새 댓글을 남겼습니다."}</p>
        <Link href={item.path} className="break-words font-medium underline underline-offset-4" onClick={() => { if (!item.read) void read(item); }}>{item.title}</Link>
        {!item.read && <div className="mt-3"><Button label="읽음 표시" size="sm" isDisabled={busy} onClick={() => void read(item)} /></div>}
      </li>)}</ul>}
      {page.nextCursor && <div className="mt-6"><Button label="이전 알림 더 보기" isLoading={busy} isDisabled={busy} onClick={() => void more()} /></div>}
    </>}
  </section>;
}

export function NotificationInboxLoading() {
  return <div className="page-skeleton space-y-4" role="status" aria-label="알림을 불러오는 중입니다"><span className="sr-only">알림을 불러오는 중입니다.</span>{[0, 1, 2].map(index => <Skeleton key={index} height={112} index={index} data-skeleton-shape />)}</div>;
}
