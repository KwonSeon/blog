"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import {
  FormField,
  FormTextarea,
  FormSelect,
  FormMessage,
  SurfaceCard,
  CTAButton,
} from "@/src/shared/ui";
import { ActionButton } from "@/src/shared/ui/action-button";
import { safeProjectUrl, type ManagedProject } from "@/src/shared/lib/projects";
export function ProjectEditor({ initial }: { initial?: ManagedProject }) {
  const router = useRouter();
  const [form, setForm] = useState({
    slug: initial?.slug ?? "",
    title: initial?.title ?? "",
    summary: initial?.summary ?? "",
    serviceUrl: initial?.serviceUrl ?? "",
    repoUrl: initial?.repoUrl ?? "",
    visibility: initial?.visibility ?? "PRIVATE",
    status: initial?.status ?? "ACTIVE",
  });
  const [pending, setPending] = useState(false),
    [error, setError] = useState("");
  const field = (key: keyof typeof form, value: string) =>
    setForm((s) => ({ ...s, [key]: value }));
  async function save(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    if (!/^[a-z0-9-]+$/.test(form.slug)) {
      setError("주소는 영문 소문자, 숫자, 하이픈으로 입력해 주세요.");
      return;
    }
    if ([form.serviceUrl, form.repoUrl].some((v) => v && !safeProjectUrl(v))) {
      setError("링크는 계정 정보가 없는 https:// 주소로 입력해 주세요.");
      return;
    }
    setPending(true);
    try {
      const res = await fetch(
        `/admin/api/projects${initial ? `/${initial.projectId}` : ""}`,
        {
          method: initial ? "PUT" : "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            ...form,
            serviceUrl: form.serviceUrl || null,
            repoUrl: form.repoUrl || null,
          }),
        },
      );
      const data = await res.json();
      if (!res.ok) throw new Error(data.message ?? "저장하지 못했습니다.");
      router.push("/admin/projects");
      router.refresh();
    } catch (e) {
      setError(e instanceof Error ? e.message : "저장하지 못했습니다.");
      setPending(false);
    }
  }
  return (
    <SurfaceCard padding="lg">
      <form onSubmit={save} className="grid gap-6">
        <FormField
          label="프로젝트 이름"
          name="title"
          value={form.title}
          onChange={(e) => field("title", e.target.value)}
          maxLength={200}
          required
          disabled={pending}
        />
        <FormField
          label="프로젝트 주소"
          name="slug"
          value={form.slug}
          onChange={(e) => field("slug", e.target.value)}
          placeholder="my-project"
          maxLength={120}
          required
          disabled={pending}
        />
        <FormTextarea
          label="프로젝트 소개"
          name="summary"
          value={form.summary}
          onChange={(e) => field("summary", e.target.value)}
          maxLength={500}
          rows={5}
          disabled={pending}
        />
        <FormField
          label="서비스 주소"
          name="serviceUrl"
          value={form.serviceUrl}
          onChange={(e) => field("serviceUrl", e.target.value)}
          placeholder="https://example.com"
          disabled={pending}
        />
        <FormField
          label="GitHub / Git 저장소 주소"
          name="repoUrl"
          value={form.repoUrl}
          onChange={(e) => field("repoUrl", e.target.value)}
          placeholder="https://github.com/owner/repository"
          disabled={pending}
        />
        <p className="text-sm text-muted-foreground">
          공개할 저장소 주소만 입력하세요. 방문자는 링크를 통해 소스 코드와
          README를 볼 수 있습니다. 저장소 권한은 변경하지 않습니다.
        </p>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormSelect
            label="공개 범위"
            value={form.visibility}
            onChange={(v) => field("visibility", v)}
            options={[
              { value: "PRIVATE", label: "비공개" },
              { value: "PUBLIC", label: "공개" },
            ]}
            disabled={pending}
          />
          <FormSelect
            label="운영 상태"
            value={form.status}
            onChange={(v) => field("status", v)}
            options={[
              { value: "ACTIVE", label: "활성" },
              { value: "INACTIVE", label: "비활성" },
            ]}
            disabled={pending}
          />
        </div>
        {error && <FormMessage variant="danger">{error}</FormMessage>}
        <div className="flex flex-wrap gap-3">
          <ActionButton type="submit" disabled={pending}>
            {pending ? "저장 중…" : "프로젝트 저장"}
          </ActionButton>
          <CTAButton href="/admin/projects" variant="secondary">
            목록으로
          </CTAButton>
        </div>
      </form>
    </SurfaceCard>
  );
}
