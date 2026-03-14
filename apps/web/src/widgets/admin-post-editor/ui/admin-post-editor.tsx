"use client";

import { useDeferredValue, useState, useTransition } from "react";
import {
  ADMIN_POST_STATUS_OPTIONS,
  ADMIN_POST_VISIBILITY_OPTIONS,
  PostMarkdown,
  type AdminPostRecord,
  type AdminPostStatus,
  type AdminPostVisibility,
} from "@/src/entities/post";
import {
  createAdminPost,
  changeAdminPostStatus,
  updateAdminPost,
} from "@/src/shared/lib/api/admin-post-api";
import {
  FormField,
  FormMessage,
  SurfaceCard,
} from "@/src/shared/ui";
import { AdminPostEditorShell } from "@/src/widgets/admin-post-editor-shell";

const INITIAL_EDITOR_STATE = {
  slug: "",
  title: "",
  excerpt: "",
  contentMd: "",
  visibility: "PUBLIC" as AdminPostVisibility,
  status: "DRAFT" as AdminPostStatus,
  lang: "ko",
  coverMediaAssetId: "",
};

function toOptionalPositiveNumber(value: string) {
  const trimmed = value.trim();

  if (!trimmed) {
    return undefined;
  }

  const parsed = Number(trimmed);

  if (!Number.isFinite(parsed) || parsed <= 0) {
    return null;
  }

  return parsed;
}

function validateEditorState(state: typeof INITIAL_EDITOR_STATE) {
  if (!state.slug.trim()) {
    return "slug를 입력하세요.";
  }

  if (!/^[a-z0-9-]+$/.test(state.slug.trim())) {
    return "slug는 소문자, 숫자, 하이픈만 사용할 수 있습니다.";
  }

  if (!state.title.trim()) {
    return "제목을 입력하세요.";
  }

  if (state.title.trim().length > 300) {
    return "제목은 300자를 넘길 수 없습니다.";
  }

  if (state.excerpt.trim().length > 600) {
    return "요약은 600자를 넘길 수 없습니다.";
  }

  if (!state.contentMd.trim()) {
    return "markdown 본문을 입력하세요.";
  }

  if (!/^[a-z]{2,3}(?:-[A-Z]{2})?$/.test(state.lang.trim())) {
    return "언어 코드는 ko 또는 en-US 형태로 입력해야 합니다.";
  }

  const coverMediaAssetId = toOptionalPositiveNumber(state.coverMediaAssetId);

  if (coverMediaAssetId === null) {
    return "coverMediaAssetId는 양수만 입력할 수 있습니다.";
  }

  return "";
}

function formatDateTime(value: string | null) {
  if (!value) {
    return "없음";
  }

  return new Date(value).toLocaleString("ko-KR");
}

export function AdminPostEditor() {
  const [editorState, setEditorState] = useState(INITIAL_EDITOR_STATE);
  const [savedPost, setSavedPost] = useState<AdminPostRecord | null>(null);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [isPending, startTransition] = useTransition();
  const deferredContentMd = useDeferredValue(editorState.contentMd);

  function updateField<K extends keyof typeof INITIAL_EDITOR_STATE>(
    key: K,
    value: (typeof INITIAL_EDITOR_STATE)[K],
  ) {
    setEditorState((current) => ({
      ...current,
      [key]: value,
    }));
  }

  function buildCreatePayload(status: AdminPostStatus) {
    const coverMediaAssetId = toOptionalPositiveNumber(editorState.coverMediaAssetId);

    return {
      slug: editorState.slug.trim(),
      title: editorState.title.trim(),
      excerpt: editorState.excerpt.trim() || undefined,
      contentMd: editorState.contentMd.trim(),
      visibility: editorState.visibility,
      status,
      lang: editorState.lang.trim(),
      coverMediaAssetId: coverMediaAssetId ?? undefined,
    };
  }

  function buildUpdatePayload() {
    const coverMediaAssetId = toOptionalPositiveNumber(editorState.coverMediaAssetId);

    return {
      slug: editorState.slug.trim(),
      title: editorState.title.trim(),
      excerpt: editorState.excerpt.trim() || undefined,
      contentMd: editorState.contentMd.trim(),
      visibility: editorState.visibility,
      lang: editorState.lang.trim(),
      coverMediaAssetId: coverMediaAssetId ?? undefined,
    };
  }

  async function submitEditor(targetStatus: AdminPostStatus) {
    const validationMessage = validateEditorState(editorState);

    if (validationMessage) {
      setSuccessMessage("");
      setErrorMessage(validationMessage);
      return;
    }

    setErrorMessage("");
    setSuccessMessage("");

    try {
      let nextPost: AdminPostRecord;

      if (!savedPost) {
        nextPost = await createAdminPost(buildCreatePayload(targetStatus));
      } else {
        nextPost = await updateAdminPost(savedPost.postId, buildUpdatePayload());

        if (targetStatus === "PUBLISHED" && nextPost.status !== "PUBLISHED") {
          nextPost = await changeAdminPostStatus(savedPost.postId, "PUBLISHED");
        }
      }

      setSavedPost(nextPost);
      setEditorState((current) => ({
        ...current,
        status: nextPost.status,
      }));
      setSuccessMessage(
        targetStatus === "PUBLISHED"
          ? "글이 발행됐습니다."
          : "초안이 저장됐습니다.",
      );
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "관리자 글 저장 중 오류가 발생했습니다.",
      );
    }
  }

  function handleSaveCurrentStatus() {
    startTransition(() => {
      void submitEditor(editorState.status);
    });
  }

  function handlePublish() {
    updateField("status", "PUBLISHED");
    startTransition(() => {
      void submitEditor("PUBLISHED");
    });
  }

  return (
    <AdminPostEditorShell
      editor={
        <SurfaceCard padding="lg">
          <p className="text-xs uppercase tracking-[0.24em] text-primary">Editor Inputs</p>
          <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
            새 글 작성
          </h2>
          <p className="mt-4 text-base leading-8 text-muted-foreground">
            markdown 본문과 공개 설정을 한 화면에서 조정하고, 저장 후 바로 발행까지
            이어갈 수 있게 구성합니다.
          </p>

          <div className="mt-8 grid gap-5">
            <FormField
              label="slug"
              name="slug"
              value={editorState.slug}
              onChange={(event) => updateField("slug", event.target.value)}
              placeholder="my-first-post"
              disabled={isPending}
            />
            <FormField
              label="제목"
              name="title"
              value={editorState.title}
              onChange={(event) => updateField("title", event.target.value)}
              placeholder="글 제목을 입력하세요"
              disabled={isPending}
            />

            <label className="grid gap-2 text-sm text-foreground">
              <span className="font-medium">요약</span>
              <textarea
                rows={4}
                value={editorState.excerpt}
                onChange={(event) => updateField("excerpt", event.target.value)}
                placeholder="공개 목록과 상세 상단에 노출될 요약을 입력하세요"
                disabled={isPending}
                className="rounded-2xl border border-input bg-background px-4 py-3 text-base text-foreground placeholder:text-muted-foreground disabled:cursor-not-allowed disabled:opacity-60"
              />
            </label>

            <div className="grid gap-5 md:grid-cols-2">
              <label className="grid gap-2 text-sm text-foreground">
                <span className="font-medium">공개 범위</span>
                <select
                  value={editorState.visibility}
                  onChange={(event) =>
                    updateField("visibility", event.target.value as AdminPostVisibility)
                  }
                  disabled={isPending}
                  className="min-h-12 rounded-2xl border border-input bg-background px-4 text-base text-foreground disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {ADMIN_POST_VISIBILITY_OPTIONS.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </label>

              <label className="grid gap-2 text-sm text-foreground">
                <span className="font-medium">상태</span>
                <select
                  value={editorState.status}
                  onChange={(event) =>
                    updateField("status", event.target.value as AdminPostStatus)
                  }
                  disabled={isPending}
                  className="min-h-12 rounded-2xl border border-input bg-background px-4 text-base text-foreground disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {ADMIN_POST_STATUS_OPTIONS.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </label>
            </div>

            <div className="grid gap-5 md:grid-cols-2">
              <FormField
                label="언어"
                name="lang"
                value={editorState.lang}
                onChange={(event) => updateField("lang", event.target.value)}
                disabled={isPending}
              />
              <FormField
                label="coverMediaAssetId"
                name="coverMediaAssetId"
                value={editorState.coverMediaAssetId}
                onChange={(event) => updateField("coverMediaAssetId", event.target.value)}
                placeholder="선택 입력"
                disabled={isPending}
              />
            </div>

            <label className="grid gap-2 text-sm text-foreground">
              <span className="font-medium">Markdown 본문</span>
              <textarea
                rows={20}
                value={editorState.contentMd}
                onChange={(event) => updateField("contentMd", event.target.value)}
                placeholder="# 제목&#10;&#10;본문을 작성하세요"
                disabled={isPending}
                className="rounded-3xl border border-input bg-background px-4 py-4 font-mono text-sm leading-7 text-foreground placeholder:text-muted-foreground disabled:cursor-not-allowed disabled:opacity-60"
              />
            </label>

            {errorMessage ? <FormMessage variant="danger">{errorMessage}</FormMessage> : null}
            {successMessage ? (
              <FormMessage variant="success">{successMessage}</FormMessage>
            ) : null}

            <FormMessage>
              최초 저장은 `POST`, 이후 수정은 `PUT`, 발행 전환은 `PATCH /status`
              흐름으로 처리합니다. `coverMediaAssetId`는 현재 단계에서는 비워둘 수
              있습니다.
            </FormMessage>
          </div>
        </SurfaceCard>
      }
      preview={
        <>
          <SurfaceCard padding="lg">
            <p className="text-xs uppercase tracking-[0.24em] text-primary">Preview</p>
            <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
              markdown preview pane
            </h2>
            <p className="mt-4 text-base leading-8 text-muted-foreground">
              입력 중인 markdown을 그대로 렌더링해 발행 전 읽기 흐름을 확인합니다.
            </p>

            <div className="mt-8 rounded-3xl border border-border bg-background/70 px-5 py-6">
              {deferredContentMd.trim() ? (
                <PostMarkdown content={deferredContentMd} />
              ) : (
                <p className="text-sm leading-7 text-muted-foreground">
                  본문을 입력하면 여기에 preview가 렌더링됩니다.
                </p>
              )}
            </div>
          </SurfaceCard>

          <SurfaceCard padding="lg">
            <p className="text-xs uppercase tracking-[0.24em] text-primary">Submit Actions</p>
            <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
              저장과 발행
            </h2>
            <div className="mt-6 flex flex-col gap-3">
              <button
                type="button"
                disabled={isPending}
                onClick={handleSaveCurrentStatus}
                className="inline-flex min-h-12 items-center justify-center rounded-full bg-primary px-6 text-sm font-medium text-primary-foreground transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {isPending
                  ? "처리 중..."
                  : editorState.status === "PUBLISHED"
                    ? "현재 상태로 저장"
                    : savedPost
                      ? "수정 내용 저장"
                      : "임시 저장"}
              </button>
              <button
                type="button"
                disabled={isPending}
                onClick={handlePublish}
                className="inline-flex min-h-12 items-center justify-center rounded-full border border-border px-6 text-sm font-medium text-foreground transition hover:bg-secondary/70 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {isPending ? "처리 중..." : "발행"}
              </button>
            </div>

            <div className="mt-6 grid gap-3 rounded-2xl bg-secondary/35 px-4 py-4 text-sm text-muted-foreground">
              <p>현재 상태: {savedPost?.status ?? editorState.status}</p>
              <p>저장된 postId: {savedPost?.postId ?? "없음"}</p>
              <p>마지막 업데이트: {formatDateTime(savedPost?.updatedAt ?? null)}</p>
            </div>
          </SurfaceCard>
        </>
      }
    />
  );
}
