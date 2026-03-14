import {
  FormMessage,
  SurfaceCard,
} from "@/src/shared/ui";
import type { AdminMediaUploadState } from "@/src/widgets/admin-post-editor/model/upload";

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

function formatUploadPhase(phase: AdminMediaUploadState["phase"]) {
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

interface AdminMediaUploadPanelProps {
  eyebrow: string;
  title: string;
  description: string;
  fileInputRef: React.RefObject<HTMLInputElement | null>;
  onFileChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onPickFile: () => void;
  selectLabel: string;
  selectDisabled: boolean;
  actionLabel: string;
  onAction: () => void;
  actionDisabled: boolean;
  selectedFileName: string | null;
  state: AdminMediaUploadState;
  onReset: () => void;
  resetDisabled: boolean;
  successMessage: string;
  idleMessage: string;
  children?: React.ReactNode;
}

export function AdminMediaUploadPanel({
  eyebrow,
  title,
  description,
  fileInputRef,
  onFileChange,
  onPickFile,
  selectLabel,
  selectDisabled,
  actionLabel,
  onAction,
  actionDisabled,
  selectedFileName,
  state,
  onReset,
  resetDisabled,
  successMessage,
  idleMessage,
  children,
}: AdminMediaUploadPanelProps) {
  return (
    <SurfaceCard padding="md" className="border border-border/80 bg-secondary/25">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p className="text-xs uppercase tracking-[0.2em] text-primary">{eyebrow}</p>
          <h3 className="mt-3 text-xl font-semibold tracking-tight text-foreground">
            {title}
          </h3>
          <p className="mt-3 text-sm leading-7 text-muted-foreground">{description}</p>
        </div>

        <div className="flex flex-wrap gap-3">
          <button
            type="button"
            onClick={onPickFile}
            disabled={selectDisabled}
            className="inline-flex min-h-11 items-center justify-center rounded-full border border-border px-5 text-sm font-medium text-foreground transition hover:bg-secondary/70 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {selectLabel}
          </button>
          <button
            type="button"
            onClick={onAction}
            disabled={actionDisabled}
            className="inline-flex min-h-11 items-center justify-center rounded-full bg-primary px-5 text-sm font-medium text-primary-foreground transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {actionLabel}
          </button>
        </div>
      </div>

      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        onChange={onFileChange}
        className="sr-only"
        tabIndex={-1}
      />

      {children}

      <div className="mt-5 grid gap-4 md:grid-cols-[minmax(0,1fr)_auto]">
        <div className="rounded-2xl border border-border bg-background/80 px-4 py-4">
          <p className="text-sm font-medium text-foreground">
            {selectedFileName ?? "선택된 이미지가 없습니다."}
          </p>
          <div className="mt-3 grid gap-1 text-xs text-muted-foreground">
            <p>namespace: {state.namespace}</p>
            <p>content-type: {state.contentType || "없음"}</p>
            <p>size: {formatFileSize(state.sizeBytes)}</p>
            <p>phase: {formatUploadPhase(state.phase)}</p>
            <p>progress: {state.progressPercent}%</p>
            <p>mediaAssetId: {state.mediaAssetId ? state.mediaAssetId : "없음"}</p>
          </div>
        </div>

        <button
          type="button"
          onClick={onReset}
          disabled={resetDisabled}
          className="inline-flex min-h-11 items-center justify-center rounded-full border border-border px-5 text-sm font-medium text-foreground transition hover:bg-secondary/70 disabled:cursor-not-allowed disabled:opacity-60"
        >
          선택 해제
        </button>
      </div>

      {state.errorMessage ? (
        <FormMessage variant="danger" className="mt-4">
          {state.errorMessage}
        </FormMessage>
      ) : null}

      {state.phase === "success" ? (
        <FormMessage variant="success" className="mt-4">
          {successMessage}
        </FormMessage>
      ) : (
        <FormMessage className="mt-4">{idleMessage}</FormMessage>
      )}
    </SurfaceCard>
  );
}
