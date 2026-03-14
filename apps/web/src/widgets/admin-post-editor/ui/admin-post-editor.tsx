"use client";

import { useDeferredValue, useRef, useState, useTransition } from "react";
import {
  ADMIN_POST_STATUS_OPTIONS,
  ADMIN_POST_VISIBILITY_OPTIONS,
  PostMarkdown,
  type AdminPostRecord,
  type AdminPostStatus,
  type AdminPostVisibility,
} from "@/src/entities/post";
import {
  buildPublicMediaContentUrl,
  type AdminMediaNamespace,
} from "@/src/shared/lib/api/admin-media-api";
import {
  changeAdminPostStatus,
  createAdminPost,
  updateAdminPost,
} from "@/src/shared/lib/api/admin-post-api";
import {
  FormField,
  FormMessage,
  FormSelect,
  FormTextarea,
  SurfaceCard,
} from "@/src/shared/ui";
import {
  createInitialAdminMediaUploadState,
  type AdminMediaUploadPhase,
  type AdminMediaUploadState,
} from "@/src/widgets/admin-post-editor/model/upload";
import { AdminPostEditorShell } from "@/src/widgets/admin-post-editor-shell";

const COVER_UPLOAD_NAMESPACE: AdminMediaNamespace = "post-cover";
const INLINE_UPLOAD_NAMESPACE: AdminMediaNamespace = "post-inline";
const MEDIA_CONTENT_URL_TEMPLATE = buildPublicMediaContentUrl(123456).replace(
  "123456",
  "{mediaAssetId}",
);

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

function formatFileSize(sizeBytes: number | null) {
  if (!sizeBytes) {
    return "없음";
  }

  if (sizeBytes >= 1024 * 1024) {
    return `${(sizeBytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  if (sizeBytes >= 1024) {
    return `${Math.round(sizeBytes / 1024)} KB`;
  }

  return `${sizeBytes} B`;
}

function formatUploadPhase(phase: AdminMediaUploadPhase) {
  switch (phase) {
    case "idle":
      return "선택 대기";
    case "presigning":
      return "presign 요청 중";
    case "uploading":
      return "storage 업로드 중";
    case "completing":
      return "업로드 완료 확인 중";
    case "success":
      return "업로드 완료";
    case "error":
      return "업로드 실패";
    default:
      return phase;
  }
}

function createUploadStateFromFile(
  file: File,
  namespace: AdminMediaNamespace,
): AdminMediaUploadState {
  const nextState = createInitialAdminMediaUploadState(namespace);

  return {
    ...nextState,
    fileName: file.name,
    contentType: file.type || "application/octet-stream",
    sizeBytes: file.size,
  };
}

function buildInlineImageTemplate(altText: string) {
  return `![${altText.trim() || "업로드 이미지"}](${MEDIA_CONTENT_URL_TEMPLATE})`;
}

export function AdminPostEditor() {
  const [editorState, setEditorState] = useState(INITIAL_EDITOR_STATE);
  const [savedPost, setSavedPost] = useState<AdminPostRecord | null>(null);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [isPending, startTransition] = useTransition();
  const [selectedCoverFile, setSelectedCoverFile] = useState<File | null>(null);
  const [selectedInlineFile, setSelectedInlineFile] = useState<File | null>(null);
  const [inlineImageAlt, setInlineImageAlt] = useState("");
  const [coverUploadState, setCoverUploadState] = useState(() =>
    createInitialAdminMediaUploadState(COVER_UPLOAD_NAMESPACE),
  );
  const [inlineUploadState, setInlineUploadState] = useState(() =>
    createInitialAdminMediaUploadState(INLINE_UPLOAD_NAMESPACE),
  );
  const deferredContentMd = useDeferredValue(editorState.contentMd);
  const coverFileInputRef = useRef<HTMLInputElement>(null);
  const inlineFileInputRef = useRef<HTMLInputElement>(null);

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

  function handleCoverFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];

    if (!file) {
      return;
    }

    setSelectedCoverFile(file);
    setCoverUploadState(createUploadStateFromFile(file, COVER_UPLOAD_NAMESPACE));
    setErrorMessage("");
  }

  function handleInlineFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];

    if (!file) {
      return;
    }

    setSelectedInlineFile(file);
    setInlineUploadState(createUploadStateFromFile(file, INLINE_UPLOAD_NAMESPACE));
    setErrorMessage("");
  }

  function clearSelectedUpload(target: "cover" | "inline") {
    if (target === "cover") {
      setSelectedCoverFile(null);
      setCoverUploadState(createInitialAdminMediaUploadState(COVER_UPLOAD_NAMESPACE));

      if (coverFileInputRef.current) {
        coverFileInputRef.current.value = "";
      }

      return;
    }

    setSelectedInlineFile(null);
    setInlineUploadState(createInitialAdminMediaUploadState(INLINE_UPLOAD_NAMESPACE));

    if (inlineFileInputRef.current) {
      inlineFileInputRef.current.value = "";
    }
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

            <FormTextarea
              label="요약"
              rows={4}
              value={editorState.excerpt}
              onChange={(event) => updateField("excerpt", event.target.value)}
              placeholder="공개 목록과 상세 상단에 노출될 요약을 입력하세요"
              disabled={isPending}
            />

            <div className="grid gap-5 md:grid-cols-2">
              <FormSelect
                label="공개 범위"
                value={editorState.visibility}
                onChange={(event) =>
                  updateField("visibility", event.target.value as AdminPostVisibility)
                }
                disabled={isPending}
                options={ADMIN_POST_VISIBILITY_OPTIONS}
              />

              <FormSelect
                label="상태"
                value={editorState.status}
                onChange={(event) =>
                  updateField("status", event.target.value as AdminPostStatus)
                }
                disabled={isPending}
                options={ADMIN_POST_STATUS_OPTIONS}
              />
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
                placeholder="업로드 후 자동 반영 또는 수동 입력"
                disabled={isPending}
              />
            </div>

            <SurfaceCard padding="md" className="border border-border/80 bg-secondary/25">
              <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                  <p className="text-xs uppercase tracking-[0.2em] text-primary">
                    Cover Upload
                  </p>
                  <h3 className="mt-3 text-xl font-semibold tracking-tight text-foreground">
                    커버 이미지 업로드 shell
                  </h3>
                  <p className="mt-3 text-sm leading-7 text-muted-foreground">
                    `post-cover` namespace로 presign 업로드를 준비하고, 완료 후
                    `coverMediaAssetId`를 자동 반영할 자리를 먼저 고정합니다.
                  </p>
                </div>

                <div className="flex flex-wrap gap-3">
                  <button
                    type="button"
                    onClick={() => coverFileInputRef.current?.click()}
                    disabled={isPending}
                    className="inline-flex min-h-11 items-center justify-center rounded-full border border-border px-5 text-sm font-medium text-foreground transition hover:bg-secondary/70 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    커버 이미지 선택
                  </button>
                  <button
                    type="button"
                    disabled
                    className="inline-flex min-h-11 items-center justify-center rounded-full bg-primary px-5 text-sm font-medium text-primary-foreground opacity-60"
                  >
                    업로드 연결 예정
                  </button>
                </div>
              </div>

              <input
                ref={coverFileInputRef}
                type="file"
                accept="image/*"
                onChange={handleCoverFileChange}
                className="sr-only"
                tabIndex={-1}
              />

              <div className="mt-5 grid gap-4 md:grid-cols-[minmax(0,1fr)_auto]">
                <div className="rounded-2xl border border-border bg-background/80 px-4 py-4">
                  <p className="text-sm font-medium text-foreground">
                    {selectedCoverFile?.name ?? "선택된 cover 이미지가 없습니다."}
                  </p>
                  <div className="mt-3 grid gap-1 text-xs text-muted-foreground">
                    <p>namespace: {coverUploadState.namespace}</p>
                    <p>content-type: {coverUploadState.contentType || "없음"}</p>
                    <p>size: {formatFileSize(coverUploadState.sizeBytes)}</p>
                    <p>phase: {formatUploadPhase(coverUploadState.phase)}</p>
                  </div>
                </div>

                <button
                  type="button"
                  onClick={() => clearSelectedUpload("cover")}
                  disabled={!selectedCoverFile || isPending}
                  className="inline-flex min-h-11 items-center justify-center rounded-full border border-border px-5 text-sm font-medium text-foreground transition hover:bg-secondary/70 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  선택 해제
                </button>
              </div>

              <FormMessage className="mt-4">
                실제 presign 요청, storage 업로드, complete 호출은 다음 단계에서
                연결합니다. 이 단계에서는 file selection, namespace, 자동 반영
                위치만 먼저 고정합니다.
              </FormMessage>
            </SurfaceCard>

            <SurfaceCard padding="md" className="border border-border/80 bg-secondary/25">
              <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                  <p className="text-xs uppercase tracking-[0.2em] text-primary">
                    Inline Image
                  </p>
                  <h3 className="mt-3 text-xl font-semibold tracking-tight text-foreground">
                    본문 이미지 삽입 shell
                  </h3>
                  <p className="mt-3 text-sm leading-7 text-muted-foreground">
                    업로드 완료 후 `post-inline` namespace 기준 이미지를 본문 caret
                    위치에 markdown 문법으로 삽입할 자리를 먼저 고정합니다.
                  </p>
                </div>

                <div className="flex flex-wrap gap-3">
                  <button
                    type="button"
                    onClick={() => inlineFileInputRef.current?.click()}
                    disabled={isPending}
                    className="inline-flex min-h-11 items-center justify-center rounded-full border border-border px-5 text-sm font-medium text-foreground transition hover:bg-secondary/70 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    본문 이미지 선택
                  </button>
                  <button
                    type="button"
                    disabled
                    className="inline-flex min-h-11 items-center justify-center rounded-full bg-primary px-5 text-sm font-medium text-primary-foreground opacity-60"
                  >
                    본문 삽입 연결 예정
                  </button>
                </div>
              </div>

              <input
                ref={inlineFileInputRef}
                type="file"
                accept="image/*"
                onChange={handleInlineFileChange}
                className="sr-only"
                tabIndex={-1}
              />

              <div className="mt-5 grid gap-5 md:grid-cols-2">
                <FormField
                  label="이미지 alt 텍스트"
                  name="inlineImageAlt"
                  value={inlineImageAlt}
                  onChange={(event) => setInlineImageAlt(event.target.value)}
                  placeholder="설명 텍스트를 입력하세요"
                  disabled={isPending}
                />

                <div className="grid gap-2 text-sm text-foreground">
                  <span className="font-medium">삽입 템플릿</span>
                  <div className="rounded-2xl border border-border bg-background/80 px-4 py-4 font-mono text-xs leading-6 text-muted-foreground">
                    {buildInlineImageTemplate(inlineImageAlt)}
                  </div>
                </div>
              </div>

              <div className="mt-5 grid gap-4 md:grid-cols-[minmax(0,1fr)_auto]">
                <div className="rounded-2xl border border-border bg-background/80 px-4 py-4">
                  <p className="text-sm font-medium text-foreground">
                    {selectedInlineFile?.name ?? "선택된 본문 이미지가 없습니다."}
                  </p>
                  <div className="mt-3 grid gap-1 text-xs text-muted-foreground">
                    <p>namespace: {inlineUploadState.namespace}</p>
                    <p>content-type: {inlineUploadState.contentType || "없음"}</p>
                    <p>size: {formatFileSize(inlineUploadState.sizeBytes)}</p>
                    <p>phase: {formatUploadPhase(inlineUploadState.phase)}</p>
                  </div>
                </div>

                <button
                  type="button"
                  onClick={() => clearSelectedUpload("inline")}
                  disabled={!selectedInlineFile || isPending}
                  className="inline-flex min-h-11 items-center justify-center rounded-full border border-border px-5 text-sm font-medium text-foreground transition hover:bg-secondary/70 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  선택 해제
                </button>
              </div>

              <FormMessage className="mt-4">
                업로드가 끝나면 `mediaAssetId`를 content endpoint URL로 바꿔 본문에
                삽입합니다. 실제 caret 위치 삽입과 완료 후 메시지는 다음 단계에서
                연결합니다.
              </FormMessage>
            </SurfaceCard>

            <FormTextarea
              label="Markdown 본문"
              rows={20}
              value={editorState.contentMd}
              onChange={(event) => updateField("contentMd", event.target.value)}
              placeholder="# 제목&#10;&#10;본문을 작성하세요"
              disabled={isPending}
              className="rounded-3xl py-4 font-mono text-sm leading-7"
            />

            {errorMessage ? <FormMessage variant="danger">{errorMessage}</FormMessage> : null}
            {successMessage ? (
              <FormMessage variant="success">{successMessage}</FormMessage>
            ) : null}

            <FormMessage>
              최초 저장은 `POST`, 이후 수정은 `PUT`, 발행 전환은 `PATCH /status`
              흐름으로 처리합니다. media 연동은 현재 shell까지 들어가 있고, 다음
              단계에서 presign 업로드와 본문 삽입 동작을 연결합니다.
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
              <p>최근 수정 시각: {formatDateTime(savedPost?.updatedAt ?? null)}</p>
              <p>발행 시각: {formatDateTime(savedPost?.publishedAt ?? null)}</p>
            </div>
          </SurfaceCard>
        </>
      }
    />
  );
}
