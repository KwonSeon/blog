import { runtimeConfig } from "@/src/shared/config/runtime";
import {
  clearAdminSession,
  getValidAdminSession,
} from "@/src/shared/lib/auth/admin-session";

interface ErrorResponse {
  message?: string;
}

export type AdminMediaNamespace =
  | "post-cover"
  | "project-cover"
  | "post-inline"
  | "project-inline";

export interface AdminMediaUploadPresignInput {
  namespace: AdminMediaNamespace;
  originalFilename: string;
  contentType: string;
  sizeBytes: number;
}

export interface AdminMediaUploadPresignResult {
  mediaAssetId: number;
  bucket: string;
  objectKey: string;
  uploadUrl: string;
  httpMethod: string;
  uploadStatus: string;
  expiresAt: string;
}

export interface AdminMediaUploadCompleteInput {
  mediaAssetId: number;
  objectKey: string;
  originalFilename: string;
}

export interface AdminMediaUploadCompleteResult {
  mediaAssetId: number;
  bucket: string;
  objectKey: string;
  originalFilename: string;
  contentType: string;
  sizeBytes: number;
  mediaType: string;
  fileExtension: string;
  uploadStatus: string;
  createdAt: string;
}

function toMediaApiUrl(path: string) {
  return `${runtimeConfig.mediaApiBaseUrl.replace(/\/$/, "")}${path}`;
}

async function requestAdminMediaApi<T>(
  path: string,
  init: RequestInit,
): Promise<T> {
  const session = getValidAdminSession();

  if (!session) {
    throw new Error("관리자 인증이 필요합니다. 다시 로그인하세요.");
  }

  const response = await fetch(toMediaApiUrl(path), {
    ...init,
    headers: {
      Authorization: `Bearer ${session.accessToken}`,
      ...(init.headers ?? {}),
    },
    cache: "no-store",
  });

  const data = (await response.json().catch(() => null)) as T | ErrorResponse | null;

  if (response.status === 401 || response.status === 403) {
    clearAdminSession();
    throw new Error("관리자 인증이 만료됐습니다. 다시 로그인하세요.");
  }

  if (!response.ok) {
    throw new Error(
      (data as ErrorResponse | null)?.message ?? "관리자 미디어 요청에 실패했습니다.",
    );
  }

  return data as T;
}

export function requestAdminMediaUploadPresign(
  input: AdminMediaUploadPresignInput,
) {
  return requestAdminMediaApi<AdminMediaUploadPresignResult>(
    "/admin/media/uploads/presign",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(input),
    },
  );
}

export function completeAdminMediaUpload(input: AdminMediaUploadCompleteInput) {
  return requestAdminMediaApi<AdminMediaUploadCompleteResult>(
    "/admin/media/uploads/complete",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(input),
    },
  );
}

export function buildPublicMediaContentUrl(mediaAssetId: number) {
  return toMediaApiUrl(`/public/media/assets/${mediaAssetId}/content`);
}
