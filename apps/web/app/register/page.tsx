"use client";
import { useState, useTransition } from "react";
import { Button } from "@astryxdesign/core/Button";
import { Container, SurfaceCard, FormField, CTAButton } from "@/src/shared/ui";
export default function Register() {
  const [email, setEmail] = useState(""),
    [code, setCode] = useState(""),
    [name, setName] = useState(""),
    [password, setPassword] = useState(""),
    [token, setToken] = useState(""),
    [sent, setSent] = useState(false),
    [done, setDone] = useState(false),
    [message, setMessage] = useState("");
  const [pending, start] = useTransition();
  function submit(action: string) {
    start(async () => {
      setMessage("");
      try {
        const response = await fetch("/api/auth/register", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            action,
            email,
            code,
            name,
            password,
            emailVerificationToken: token,
          }),
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message);
        if (action === "request") {
          setSent(true);
          setMessage("인증번호를 보냈습니다.");
        }
        if (action === "confirm") {
          setToken(data.verificationToken);
          setMessage("이메일 인증을 완료했습니다.");
        }
        if (action === "register") setDone(true);
      } catch (error) {
        setMessage(
          error instanceof Error ? error.message : "다시 시도해 주세요.",
        );
      }
    });
  }
  return (
    <Container size="narrow" className="py-16">
      <SurfaceCard padding="lg">
        <h1 className="text-3xl font-semibold">통합 계정 만들기</h1>
        {done ? (
          <div className="grid gap-5 mt-6">
            <p>계정을 만들었습니다. 통합 로그인으로 계속해 주세요.</p>
            <CTAButton href="/api/auth/sso/start">로그인</CTAButton>
          </div>
        ) : (
          <div className="grid gap-5 mt-6">
            <FormField
              label="이메일"
              type="email"
              value={email}
              disabled={pending || !!token}
              onChange={(e) => {
                setEmail(e.target.value);
                setToken("");
                setSent(false);
              }}
              autoComplete="email"
            />
            <Button
              label={sent ? "인증번호 다시 받기" : "인증번호 받기"}
              isDisabled={pending || !!token || !email}
              onClick={() => submit("request")}
            />
            {sent && !token ? (
              <>
                <FormField
                  label="인증번호"
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                  autoComplete="one-time-code"
                />
                <Button
                  label="이메일 확인"
                  isDisabled={pending || code.length !== 6}
                  onClick={() => submit("confirm")}
                />
              </>
            ) : null}
            {token ? (
              <>
                <FormField
                  label="이름"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  autoComplete="name"
                />
                <FormField
                  label="비밀번호"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  autoComplete="new-password"
                />
                <Button
                  label="계정 만들기"
                  variant="primary"
                  isLoading={pending}
                  isDisabled={!name || password.length < 8}
                  onClick={() => submit("register")}
                />
              </>
            ) : null}
            <p role="status">{message}</p>
            <CTAButton href="/admin/login" variant="secondary">
              이미 계정이 있어요
            </CTAButton>
          </div>
        )}
      </SurfaceCard>
    </Container>
  );
}
