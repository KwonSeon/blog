"use client";
import { Button } from "@astryxdesign/core/Button";
export function AdminLoginForm() {
  return (
    <div className="grid gap-6">
      <h1 className="text-3xl font-semibold">블로그에 로그인</h1>
      <p className="text-muted-foreground">
        공통 계정으로 로그인하세요. 글 작성과 발행은 블로그 작성자에게
        제공됩니다.
      </p>
      <Button
        href="/api/auth/sso/start"
        label="통합 계정으로 로그인"
        variant="primary"
        size="lg"
      />
      <Button href="/register" label="통합 계정 만들기" variant="secondary" />
      <p className="text-sm text-muted-foreground">
        이메일·Google·카카오 로그인은 통합 계정 화면에서 이용할 수 있습니다.
      </p>
    </div>
  );
}
