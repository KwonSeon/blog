"use client";
import { useEffect, useRef, useState } from "react";
import { Button } from "@astryxdesign/core/Button";
import { TextArea } from "@astryxdesign/core/TextArea";
import { commentTree, type CommentItem } from "@/src/shared/lib/comments/tree";
type Page = { items: CommentItem[]; nextCursor: string | null; authenticated: boolean };
export function ContentComments({ kind, id, path }: { kind: "posts" | "projects"; id: string; path: string }) {
  const [page, setPage] = useState<Page | null>(null), [text, setText] = useState(""), [error, setError] = useState(""), [notice, setNotice] = useState("");
  const [target, setTarget] = useState<{ item: CommentItem; edit: boolean } | null>(null), [confirm, setConfirm] = useState<string | null>(null), [busy, setBusy] = useState(false);
  const lock = useRef(false), key = useRef<string | null>(null), field = useRef<HTMLTextAreaElement>(null);
  const endpoint = `/api/community/comments/${kind}/${id}`;
  useEffect(() => { const controller = new AbortController(); fetch(endpoint, { cache: "no-store", signal: controller.signal }).then(async r => { if (!r.ok) throw new Error(); setPage(await r.json()); }).catch(() => { if (!controller.signal.aborted) setError("댓글을 불러오지 못했습니다. 새로고침해 주세요."); }); return () => controller.abort(); }, [endpoint]);
  function failure(status: number) { return status === 429 ? "작성 횟수가 많습니다. 잠시 후 다시 시도해 주세요." : status === 409 ? "이미 등록했거나 변경된 댓글입니다. 내용을 확인해 주세요." : status === 401 ? "다시 로그인해 주세요." : status === 403 ? "이 댓글을 변경할 권한이 없습니다." : "저장하지 못했습니다. 입력한 내용은 유지됩니다."; }
  function choose(item: CommentItem, edit: boolean) { setTarget({ item, edit }); setText(edit ? item.body : ""); key.current = null; setError(""); field.current?.focus(); field.current?.scrollIntoView({ block: "center", behavior: "smooth" }); }
  async function send() {
    if (lock.current || !text.trim()) return; lock.current = true; setBusy(true); setError(""); setNotice(""); key.current ??= crypto.randomUUID();
    try {
      const r = await fetch(target?.edit ? `${endpoint}/${target.item.id}` : endpoint, { method: target?.edit ? "PATCH" : "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ body: text, parentId: target?.item.id ?? null, requestKey: key.current }) });
      if (!r.ok) { if (r.status === 401) setPage(p => p ? { ...p, authenticated: false } : p); throw new Error(failure(r.status)); }
      const item: CommentItem = await r.json(); setPage(p => p ? { ...p, items: [...p.items.filter(x => x.id !== item.id), item] } : p); setText(""); setTarget(null); key.current = null; setNotice("댓글을 저장했습니다.");
    } catch (e) { setError(e instanceof Error ? e.message : "저장하지 못했습니다."); } finally { lock.current = false; setBusy(false); }
  }
  async function remove(item: CommentItem) {
    if (lock.current) return; lock.current = true; setBusy(true); setError("");
    try { const r = await fetch(`${endpoint}/${item.id}`, { method: "DELETE" }); if (!r.ok) throw new Error(failure(r.status)); const changed: CommentItem = await r.json(); setPage(p => p ? { ...p, items: p.items.map(x => x.id === item.id ? changed : x) } : p); setConfirm(null); if (target?.item.id === item.id) { setTarget(null); setText(""); } setNotice("댓글을 삭제했습니다. 답글은 유지됩니다."); }
    catch (e) { setError(e instanceof Error ? e.message : "삭제하지 못했습니다."); } finally { lock.current = false; setBusy(false); }
  }
  async function more() {
    if (lock.current || !page?.nextCursor) return; lock.current = true; setBusy(true); setError("");
    try { const r = await fetch(`${endpoint}?after=${page.nextCursor}`, { cache: "no-store" }); if (!r.ok) throw new Error(); const next: Page = await r.json(); setPage(p => ({ ...next, items: [...(p?.items || []), ...next.items] })); }
    catch { setError("댓글을 더 불러오지 못했습니다. 다시 눌러 주세요."); } finally { lock.current = false; setBusy(false); }
  }
  return <section aria-label="댓글" className="my-12 border-t border-border pt-8">
    <h2 className="mb-5 text-2xl font-semibold">댓글</h2>
    {page?.authenticated ? <form onSubmit={e => { e.preventDefault(); void send(); }} className="mb-6 space-y-3">
      {target && <p className="text-sm text-muted-foreground">{target.edit ? "댓글 수정" : `${target.item.author}님에게 답글`}</p>}
      <TextArea ref={field} label="댓글 내용" value={text} onChange={value => { setText(value); key.current = null; }} htmlMaxLength={2000} rows={3} width="100%" isRequired isDisabled={busy} description="최대 2,000자 · 개인정보나 광고성 내용은 남기지 말아 주세요." />
      <div className="flex gap-2"><Button type="submit" label={target?.edit ? "수정 저장" : target ? "답글 등록" : "댓글 등록"} isLoading={busy} isDisabled={busy || !text.trim()} size="lg" />{target && <Button label="작성 취소" isDisabled={busy} onClick={() => { setTarget(null); setText(""); key.current = null; }} />}</div>
    </form> : page ? <Button label="로그인하고 댓글 쓰기" href={`/api/auth/sso/start?next=${encodeURIComponent(path)}`} size="lg" /> : <p className="text-sm text-muted-foreground">댓글을 불러오는 중입니다.</p>}
    {error && <p role="alert" className="my-4 text-sm text-destructive">{error}</p>}{notice && <p role="status" className="my-4 text-sm text-muted-foreground">{notice}</p>}
    {page && !page.items.length && <p className="my-6 text-muted-foreground">첫 댓글을 남겨 주세요.</p>}
    <ol className="mt-6 space-y-4">{commentTree(page?.items || []).map(item => <li key={item.id} id={`comment-${item.id}`} style={{ marginInlineStart: `${Math.min(item.depth, 3) * 12}px` }} className="rounded-lg border border-border p-4">
      {item.parentId && <a href={`#comment-${item.parentId}`} className="text-xs text-muted-foreground underline">이전 댓글에 대한 답글{item.depth > 3 ? ` · ${item.depth}단계` : ""}</a>}
      <div className="flex flex-wrap items-center gap-2 text-sm"><strong>{item.author}</strong>{item.canEdit && <span className="text-muted-foreground">내 댓글</span>}<time className="text-xs text-muted-foreground" dateTime={item.createdAt}>{new Date(item.createdAt).toLocaleDateString("ko-KR")}</time></div>
      <p className="my-3 whitespace-pre-wrap break-words text-sm leading-6">{item.deleted ? "삭제된 댓글입니다." : item.body}</p>
      <div className="flex flex-wrap gap-2">{page?.authenticated && <Button label="답글" size="sm" isDisabled={busy} onClick={() => choose(item, false)} />}{item.canEdit && <Button label="수정" size="sm" isDisabled={busy} onClick={() => choose(item, true)} />}{item.canDelete && <Button label="삭제" size="sm" isDisabled={busy} onClick={() => setConfirm(item.id)} />}</div>
      {confirm === item.id && <div className="mt-3 flex flex-wrap items-center gap-2"><span className="text-sm">댓글을 삭제할까요? 답글은 유지됩니다.</span><Button label="삭제 확인" size="sm" isDisabled={busy} onClick={() => void remove(item)} /><Button label="취소" size="sm" isDisabled={busy} onClick={() => setConfirm(null)} /></div>}
    </li>)}</ol>
    {page?.nextCursor && <div className="mt-6"><Button label="댓글 더 보기" isLoading={busy} isDisabled={busy} onClick={() => void more()} /></div>}
  </section>;
}
