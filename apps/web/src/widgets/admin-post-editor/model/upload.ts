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
  mediaAssetId: number | null;
  objectKey: string;
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
    mediaAssetId: null,
    objectKey: "",
    errorMessage: "",
    completedAt: null,
  };
}
