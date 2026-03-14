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
  completeAdminMediaUpload,
  requestAdminMediaUploadPresign,
  uploadFileToPresignedUrl,
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
  type AdminMediaUploadState,
} from "@/src/widgets/admin-post-editor/model/upload";
import { AdminPostEditorShell } from "@/src/widgets/admin-post-editor-shell";
import { AdminMediaUploadPanel } from "./admin-media-upload-panel";

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

function buildInlineImageMarkdown(
  mediaAssetId: number,
  altText: string,
  fileName: string,
) {
  const resolvedAltText =
    altText.trim() || fileName.replace(/\.[^.]+$/, "") || "업로드 이미지";

  return `![${resolvedAltText}](${buildPublicMediaContentUrl(mediaAssetId)})`;
}

function isUploadBusy(state: AdminMediaUploadState) {
  return (
    state.phase === "presigning" ||
    state.phase === "uploading" ||
    state.phase === "completing"
  );
}

function getUploadButtonLabel(
  state: AdminMediaUploadState,
  idleLabel: string,
  successLabel: string,
) {
  switch (state.phase) {
    case "presigning":
      return "presign 요청 중...";
    case "uploading":
      return `업로드 중... ${state.progressPercent}%`;
    case "completing":
      return "완료 처리 중...";
    case "success":
      return successLabel;
    case "error":
      return "다시 시도";
    default:
      return idleLabel;
  }
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
  const markdownTextareaRef = useRef<HTMLTextAreaElement>(null);

  function updateField<K extends keyof typeof INITIAL_EDITOR_STATE>(
    key: K,
    value: (typeof INITIAL_EDITOR_STATE)[K],
  ) {
    setEditorState((current) => ({
      ...current,
      [key]: value,
    }));
  }

  function updateUploadState(
    target: "cover" | "inline",
    updater: (current: AdminMediaUploadState) => AdminMediaUploadState,
  ) {
    if (target === "cover") {
      setCoverUploadState((current) => updater(current));
      return;
    }

    setInlineUploadState((current) => updater(current));
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

  async function runMediaUpload(target: "cover" | "inline") {
    const file = target === "cover" ? selectedCoverFile : selectedInlineFile;
    const namespace =
      target === "cover" ? COVER_UPLOAD_NAMESPACE : INLINE_UPLOAD_NAMESPACE;

    if (!file) {
      throw new Error(
        target === "cover"
          ? "업로드할 커버 이미지를 먼저 선택하세요."
          : "삽입할 본문 이미지를 먼저 선택하세요.",
      );
    }

    setErrorMessage("");
    setSuccessMessage("");

    updateUploadState(target, (current) => ({
      ...current,
      phase: "presigning",
      progressPercent: 0,
      errorMessage: "",
    }));

    try {
      const presigned = await requestAdminMediaUploadPresign({
        namespace,
        originalFilename: file.name,
        contentType: file.type || "application/octet-stream",
        sizeBytes: file.size,
      });

      updateUploadState(target, (current) => ({
        ...current,
        phase: "uploading",
        mediaAssetId: presigned.mediaAssetId,
        objectKey: presigned.objectKey,
        errorMessage: "",
      }));

      await uploadFileToPresignedUrl({
        uploadUrl: presigned.uploadUrl,
        file,
        contentType: file.type || "application/octet-stream",
        onProgress: (percent) => {
          updateUploadState(target, (current) => ({
            ...current,
            phase: "uploading",
            progressPercent: percent,
          }));
        },
      });

      updateUploadState(target, (current) => ({
        ...current,
        phase: "completing",
        progressPercent: 100,
      }));

      const completed = await completeAdminMediaUpload({
        mediaAssetId: presigned.mediaAssetId,
        objectKey: presigned.objectKey,
        originalFilename: file.name,
      });

      const publicUrl = buildPublicMediaContentUrl(completed.mediaAssetId);

      updateUploadState(target, (current) => ({
        ...current,
        phase: "success",
        progressPercent: 100,
        mediaAssetId: completed.mediaAssetId,
        objectKey: completed.objectKey,
        publicUrl,
        completedAt: completed.createdAt,
      }));

      return {
        file,
        completed,
        publicUrl,
      };
    } catch (error) {
      const message =
        error instanceof Error
          ? error.message
          : "이미지 업로드 중 오류가 발생했습니다.";

      updateUploadState(target, (current) => ({
        ...current,
        phase: "error",
        errorMessage: message,
      }));

      throw error;
    }
  }

  function insertMarkdownAtCursor(snippet: string) {
    const textarea = markdownTextareaRef.current;
    const currentValue = editorState.contentMd;
    const selectionStart = textarea?.selectionStart ?? currentValue.length;
    const selectionEnd = textarea?.selectionEnd ?? currentValue.length;
    const before = currentValue.slice(0, selectionStart);
    const after = currentValue.slice(selectionEnd);
    const prefix =
      before.length === 0
        ? ""
        : before.endsWith("\n\n")
          ? ""
          : before.endsWith("\n")
            ? "\n"
            : "\n\n";
    const suffix =
      after.length === 0
        ? ""
        : after.startsWith("\n\n")
          ? ""
          : after.startsWith("\n")
            ? "\n"
            : "\n\n";
    const nextContent = `${before}${prefix}${snippet}${suffix}${after}`;
    const nextCursor = (before + prefix + snippet).length;

    setEditorState((current) => ({
      ...current,
      contentMd: nextContent,
    }));

    requestAnimationFrame(() => {
      const nextTextarea = markdownTextareaRef.current;

      if (!nextTextarea) {
        return;
      }

      nextTextarea.focus();
      nextTextarea.setSelectionRange(nextCursor, nextCursor);
    });
  }

  async function handleCoverUpload() {
    try {
      const result = await runMediaUpload("cover");

      if (!result) {
        return;
      }

      updateField("coverMediaAssetId", String(result.completed.mediaAssetId));
      setSuccessMessage(
        `커버 이미지 업로드가 완료돼 coverMediaAssetId ${result.completed.mediaAssetId}를 반영했습니다.`,
      );
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "커버 이미지 업로드 중 오류가 발생했습니다.",
      );
    }
  }

  async function handleInlineImageInsert() {
    try {
      const result = await runMediaUpload("inline");

      if (!result) {
        return;
      }

      const markdown = buildInlineImageMarkdown(
        result.completed.mediaAssetId,
        inlineImageAlt,
        result.file.name,
      );

      insertMarkdownAtCursor(markdown);
      setSuccessMessage(
        `본문 이미지 업로드가 완료돼 mediaAssetId ${result.completed.mediaAssetId}를 본문에 삽입했습니다.`,
      );
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "본문 이미지 삽입 중 오류가 발생했습니다.",
      );
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
                disabled={isPending || isUploadBusy(coverUploadState)}
              />
            </div>

            <AdminMediaUploadPanel
              eyebrow="Cover Upload"
              title="커버 이미지 업로드"
              description="`post-cover` namespace로 presign 업로드를 수행하고, complete 응답의 `mediaAssetId`를 `coverMediaAssetId`에 자동 반영합니다."
              fileInputRef={coverFileInputRef}
              onFileChange={handleCoverFileChange}
              onPickFile={() => coverFileInputRef.current?.click()}
              selectLabel="커버 이미지 선택"
              selectDisabled={isPending || isUploadBusy(coverUploadState)}
              actionLabel={getUploadButtonLabel(
                coverUploadState,
                "커버 이미지 업로드",
                "다시 업로드",
              )}
              onAction={() => void handleCoverUpload()}
              actionDisabled={
                !selectedCoverFile || isPending || isUploadBusy(coverUploadState)
              }
              selectedFileName={selectedCoverFile?.name ?? null}
              state={coverUploadState}
              onReset={() => clearSelectedUpload("cover")}
              resetDisabled={
                !selectedCoverFile || isPending || isUploadBusy(coverUploadState)
              }
              successMessage={`mediaAssetId ${coverUploadState.mediaAssetId} 업로드가 완료됐고, \`coverMediaAssetId\` 입력란에 자동 반영할 준비가 끝났습니다.`}
              idleMessage="업로드가 완료되면 complete 응답의 `mediaAssetId`를 바로 `coverMediaAssetId`에 채웁니다."
            />

            <AdminMediaUploadPanel
              eyebrow="Inline Image"
              title="본문 이미지 업로드와 삽입"
              description="업로드 완료 후 `post-inline` namespace 기준 이미지를 markdown 본문 현재 caret 위치에 삽입합니다."
              fileInputRef={inlineFileInputRef}
              onFileChange={handleInlineFileChange}
              onPickFile={() => inlineFileInputRef.current?.click()}
              selectLabel="본문 이미지 선택"
              selectDisabled={isPending || isUploadBusy(inlineUploadState)}
              actionLabel={getUploadButtonLabel(
                inlineUploadState,
                "업로드 후 본문 삽입",
                "다시 업로드 후 삽입",
              )}
              onAction={() => void handleInlineImageInsert()}
              actionDisabled={
                !selectedInlineFile || isPending || isUploadBusy(inlineUploadState)
              }
              selectedFileName={selectedInlineFile?.name ?? null}
              state={inlineUploadState}
              onReset={() => clearSelectedUpload("inline")}
              resetDisabled={
                !selectedInlineFile || isPending || isUploadBusy(inlineUploadState)
              }
              successMessage={`mediaAssetId ${inlineUploadState.mediaAssetId} 업로드가 완료됐고, markdown 본문에 content URL을 바로 삽입할 수 있습니다.`}
              idleMessage="업로드가 끝나면 `mediaAssetId`를 content endpoint URL로 바꿔 본문 현재 위치에 삽입합니다."
            >
              <div className="mt-5 grid gap-5 md:grid-cols-2">
                <FormField
                  label="이미지 alt 텍스트"
                  name="inlineImageAlt"
                  value={inlineImageAlt}
                  onChange={(event) => setInlineImageAlt(event.target.value)}
                  placeholder="설명 텍스트를 입력하세요"
                  disabled={isPending || isUploadBusy(inlineUploadState)}
                />

                <div className="grid gap-2 text-sm text-foreground">
                  <span className="font-medium">삽입 템플릿</span>
                  <div className="rounded-2xl border border-border bg-background/80 px-4 py-4 font-mono text-xs leading-6 text-muted-foreground">
                    {buildInlineImageTemplate(inlineImageAlt)}
                  </div>
                </div>
              </div>
            </AdminMediaUploadPanel>

            <FormTextarea
              ref={markdownTextareaRef}
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
              흐름으로 처리합니다. media 업로드는 presign -&gt; object storage PUT -&gt;
              complete 순서로 분리해 호출합니다.
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
              <p>coverMediaAssetId: {editorState.coverMediaAssetId || "없음"}</p>
            </div>
          </SurfaceCard>
        </>
      }
    />
  );
}
