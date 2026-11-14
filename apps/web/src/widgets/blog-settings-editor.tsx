"use client";
import { useEffect, useRef, useState } from "react";
import {
  FormField,
  FormTextarea,
  FormSelect,
  FormMessage,
  SurfaceCard,
} from "@/src/shared/ui";
import { ActionButton } from "@/src/shared/ui/action-button";
import {
  defaultSiteSettings,
  type SiteSettings,
} from "@/src/shared/lib/site-settings";
export function BlogSettingsEditor({ initial }: { initial: SiteSettings }) {
  const [form, setForm] = useState(initial),
    [saved, setSaved] = useState(initial),
    [pending, setPending] = useState(false),
    [error, setError] = useState(""),
    [message, setMessage] = useState(""),
    [mobile, setMobile] = useState(false);
  const frame = useRef<HTMLIFrameElement>(null);
  useEffect(() => {
    const send = () =>
      frame.current?.contentWindow?.postMessage(
        { type: "blog-preview-settings", settings: form },
        window.location.origin,
      );
    const ready = (e: MessageEvent) => {
      if (
        e.origin === window.location.origin &&
        e.source === frame.current?.contentWindow &&
        e.data?.type === "blog-preview-ready"
      )
        send();
    };
    window.addEventListener("message", ready);
    send();
    return () => window.removeEventListener("message", ready);
  }, [form]);
  const dirty = JSON.stringify(form) !== JSON.stringify(saved);
  useEffect(() => {
    if (!dirty) return;
    const warn = (e: BeforeUnloadEvent) => {
      e.preventDefault();
      e.returnValue = "";
    };
    window.addEventListener("beforeunload", warn);
    return () => window.removeEventListener("beforeunload", warn);
  }, [dirty]);
  const field = <K extends keyof SiteSettings>(
    key: K,
    value: SiteSettings[K],
  ) => {
    setForm((s) => ({ ...s, [key]: value }));
    setMessage("");
  };
  function preset(kind: "journal" | "portfolio" | "minimal") {
    setForm((s) => ({
      ...s,
      showHero: kind !== "minimal",
      showPosts: true,
      showProjects: kind !== "minimal",
      homeOrder: kind === "portfolio" ? "PROJECTS_FIRST" : "POSTS_FIRST",
      postLayout: kind === "minimal" ? "LIST" : "GRID",
      contentWidth: kind === "portfolio" ? "WIDE" : "STANDARD",
    }));
    setMessage("미리보기에 적용했습니다. 저장하면 공개 화면에 반영됩니다.");
  }
  async function save(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    setMessage("");
    if (!form.showHero && !form.showPosts && !form.showProjects) {
      setError("홈에 표시할 영역을 하나 이상 선택해 주세요.");
      return;
    }
    if (
      form.githubUrl &&
      !/^https:\/\/github\.com\/[A-Za-z0-9_./-]+$/.test(form.githubUrl)
    ) {
      setError(
        "GitHub 링크는 https://github.com/ 으로 시작하는 주소를 입력해 주세요.",
      );
      return;
    }
    setPending(true);
    try {
      const r = await fetch("/admin/api/site-settings", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      if (r.status === 409)
        throw new Error(
          "다른 화면에서 설정이 변경됐습니다. 저장된 설정을 다시 불러온 뒤 수정해 주세요.",
        );
      if (!r.ok)
        throw new Error(
          "설정을 저장하지 못했습니다. 입력값과 로그인 상태를 확인해 주세요.",
        );
      const next = (await r.json()) as SiteSettings;
      setForm(next);
      setSaved(next);
      setMessage("저장했습니다. 블로그에 바로 반영되었습니다.");
    } catch (e) {
      setError(e instanceof Error ? e.message : "저장 실패");
    } finally {
      setPending(false);
    }
  }
  async function reload() {
    setPending(true);
    setError("");
    try {
      const r = await fetch("/admin/api/site-settings", { cache: "no-store" });
      if (!r.ok) throw new Error("저장된 설정을 불러오지 못했습니다.");
      const next = (await r.json()) as SiteSettings;
      setForm(next);
      setSaved(next);
      setMessage("저장된 설정을 불러왔습니다.");
    } catch (e) {
      setError(e instanceof Error ? e.message : "불러오기 실패");
    } finally {
      setPending(false);
    }
  }
  const visibilityOptions = [
    { value: "show", label: "표시" },
    { value: "hide", label: "숨김" },
  ];
  return (
    <div className="grid gap-8">
      <SurfaceCard padding="lg">
        <h2 className="text-xl font-semibold">빠른 레이아웃 선택</h2>
        <p className="mt-3 text-muted-foreground">
          소개 문구는 유지하고 화면 구성만 바꿉니다.
        </p>
        <div className="mt-5 flex flex-wrap gap-3">
          <ActionButton disabled={pending} onClick={() => preset("journal")}>
            글 중심
          </ActionButton>
          <ActionButton disabled={pending} onClick={() => preset("portfolio")}>
            프로젝트 중심
          </ActionButton>
          <ActionButton disabled={pending} onClick={() => preset("minimal")}>
            간결한 목록
          </ActionButton>
        </div>
      </SurfaceCard>
      <form onSubmit={save} className="grid gap-6">
        <div className="grid gap-6 xl:grid-cols-2">
          <SurfaceCard padding="lg">
            <h2 className="text-xl font-semibold mb-6">블로그 기본 정보</h2>
            <div className="grid gap-5">
              <FormField
                label="블로그 이름"
                name="siteTitle"
                value={form.siteTitle}
                onChange={(e) => field("siteTitle", e.target.value)}
                maxLength={80}
                required
                disabled={pending}
              />
              <FormTextarea
                label="홈 소개 제목"
                name="heroTitle"
                value={form.heroTitle}
                onChange={(e) => field("heroTitle", e.target.value)}
                rows={3}
                maxLength={160}
                required
                disabled={pending}
              />
              <FormTextarea
                label="홈 소개 문구"
                name="heroDescription"
                value={form.heroDescription}
                onChange={(e) => field("heroDescription", e.target.value)}
                rows={4}
                maxLength={600}
                disabled={pending}
              />
              <FormField
                label="GitHub 프로필·저장소 링크 (선택)"
                name="githubUrl"
                value={form.githubUrl}
                onChange={(e) => field("githubUrl", e.target.value)}
                disabled={pending}
              />
            </div>
          </SurfaceCard>
          <SurfaceCard padding="lg">
            <h2 className="text-xl font-semibold mb-6">레이아웃과 홈 구성</h2>
            <div className="grid gap-5">
              <FormSelect
                label="섹션 순서"
                value={form.homeOrder}
                onChange={(v) =>
                  field("homeOrder", v as SiteSettings["homeOrder"])
                }
                options={[
                  { value: "POSTS_FIRST", label: "최신 글 → 프로젝트" },
                  { value: "PROJECTS_FIRST", label: "프로젝트 → 최신 글" },
                ]}
                disabled={pending}
              />
              <FormSelect
                label="글 목록 배치"
                value={form.postLayout}
                onChange={(v) =>
                  field("postLayout", v as SiteSettings["postLayout"])
                }
                options={[
                  { value: "GRID", label: "카드형" },
                  { value: "LIST", label: "목록형" },
                ]}
                disabled={pending}
              />
              <p className="text-sm text-muted-foreground">
                홈의 최신 글과 전체 글 목록에 함께 적용됩니다.
              </p>
              <FormSelect
                label="화면 너비"
                value={form.contentWidth}
                onChange={(v) =>
                  field("contentWidth", v as SiteSettings["contentWidth"])
                }
                options={[
                  { value: "STANDARD", label: "기본" },
                  { value: "WIDE", label: "넓게" },
                ]}
                disabled={pending}
              />
              {(
                [
                  { key: "showHero", label: "홈 소개" },
                  { key: "showPosts", label: "최신 글" },
                  { key: "showProjects", label: "프로젝트" },
                ] as const
              ).map((item) => (
                <FormSelect
                  key={item.key}
                  label={item.label}
                  value={form[item.key] ? "show" : "hide"}
                  onChange={(v) => field(item.key, v === "show")}
                  options={visibilityOptions}
                  disabled={pending}
                />
              ))}
            </div>
          </SurfaceCard>
        </div>
        <SurfaceCard>
          <div className="flex flex-wrap items-center gap-3">
            <ActionButton type="submit" disabled={pending || !dirty}>
              {pending ? "저장 중…" : "변경사항 저장"}
            </ActionButton>
            <ActionButton disabled={pending} onClick={() => void reload()}>
              저장된 설정 다시 불러오기
            </ActionButton>
            <ActionButton
              disabled={pending}
              onClick={() =>
                setForm({ ...defaultSiteSettings, revision: form.revision })
              }
            >
              기본 구성 불러오기
            </ActionButton>
            <span role="status" className="text-sm text-muted-foreground">
              {dirty ? "아직 저장하지 않은 변경사항" : "저장된 설정과 같습니다"}
            </span>
          </div>
          {error && (
            <div className="mt-4">
              <FormMessage variant="danger">{error}</FormMessage>
            </div>
          )}
          {message && (
            <div className="mt-4">
              <FormMessage variant="success">{message}</FormMessage>
            </div>
          )}
        </SurfaceCard>
      </form>
      <section aria-label="레이아웃 미리보기">
        <div className="flex flex-wrap justify-between items-center gap-4 mb-5">
          <div>
            <h2 className="text-xl font-semibold">실제 콘텐츠로 미리보기</h2>
            <p className="mt-2 text-sm text-muted-foreground">
              저장하기 전에는 방문자 화면이 바뀌지 않습니다. 미리보기 안의
              링크는 비활성화됩니다.
            </p>
          </div>
          <div className="flex gap-2">
            <ActionButton disabled={!mobile} onClick={() => setMobile(false)}>
              데스크톱
            </ActionButton>
            <ActionButton disabled={mobile} onClick={() => setMobile(true)}>
              모바일
            </ActionButton>
          </div>
        </div>
        <div className="rounded-2xl border border-border bg-secondary/20 p-2 sm:p-4 overflow-x-auto">
          <iframe
            ref={frame}
            title="블로그 레이아웃 미리보기"
            src="/admin-preview"
            data-layout-preview
            data-preview-device={mobile ? "mobile" : "desktop"}
            className="block bg-background mx-auto rounded-xl border-0"
            style={{
              width: mobile ? 390 : "100%",
              maxWidth: "100%",
              height: 700,
            }}
          />
        </div>
      </section>
    </div>
  );
}
