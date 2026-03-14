import type { AdminMediaNamespace } from "@/src/shared/lib/api/admin-media-api";

export type AdminMediaUploadPhase =
  | "idle"
  | "presigning"
  | "uploading"
  | "completing"
  | "success"
  | "error";

export interface AdminMediaUploadState {
  namespace: AdminMediaNamespace;
  phase: AdminMediaUploadPhase;
  fileName: string;
  contentType: string;
  sizeBytes: number | null;
  progressPercent: number;
  mediaAssetId: number | null;
  objectKey: string;
  publicUrl: string;
  errorMessage: string;
  completedAt: string | null;
}

export function createInitialAdminMediaUploadState(
  namespace: AdminMediaNamespace,
): AdminMediaUploadState {
  return {
    namespace,
    phase: "idle",
    fileName: "",
    contentType: "",
    sizeBytes: null,
    progressPercent: 0,
    mediaAssetId: null,
    objectKey: "",
    publicUrl: "",
    errorMessage: "",
    completedAt: null,
  };
}
