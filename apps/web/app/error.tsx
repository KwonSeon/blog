"use client";
import { Button } from "@astryxdesign/core/Button";
export default function ErrorPage({ reset }: { reset: () => void }) {
  return (
    <main className="mx-auto max-w-xl px-6 py-24">
      <h1 className="text-2xl font-semibold">잠시 연결이 원활하지 않습니다</h1>
      <p className="my-5">
        다시 시도해 주세요. 작성 중인 내용이 있다면 창을 닫지 말고 잠시 기다려
        주세요.
      </p>
      <Button label="다시 시도" onClick={reset} />
    </main>
  );
}
