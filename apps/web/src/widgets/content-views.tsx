"use client";
import { useEffect, useState } from "react";
export function ContentViews({ kind, id }: { kind: "posts" | "projects"; id: string }) {
  const [count, setCount] = useState<number | null>(null);
  useEffect(() => {
    const controller = new AbortController(); let timer: ReturnType<typeof setTimeout> | undefined; let sent = false;
    const endpoint = `/api/community/views/${kind}/${id}`;
    const load = async (method: "GET" | "POST") => {
      try {
        const response = await fetch(endpoint, { method, cache: "no-store", signal: controller.signal });
        if (response.ok) { const value = await response.json(); if (!controller.signal.aborted && (method === "POST" || !sent)) setCount(value.count); }
      } catch { /* Metrics must never prevent reading. */ }
    };
    void load("GET");
    const schedule = () => {
      if (timer) clearTimeout(timer);
      if (!sent && document.visibilityState === "visible") timer = setTimeout(() => { sent = true; void load("POST"); }, 2000);
    };
    schedule(); document.addEventListener("visibilitychange", schedule);
    return () => { controller.abort(); if (timer) clearTimeout(timer); document.removeEventListener("visibilitychange", schedule); };
  }, [kind, id]);
  return count === null ? null : <p className="my-4 text-sm text-muted-foreground" aria-label="콘텐츠 조회수">조회 {count.toLocaleString("ko-KR")}회</p>;
}
