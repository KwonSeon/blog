import type {
  AdminPostRecord,
  AdminPostStatus,
  AdminPostVisibility,
} from "@/src/entities/post";
import { runtimeConfig } from "@/src/shared/config/runtime";
import {
  clearAdminSession,
  getValidAdminSession,
} from "@/src/shared/lib/auth/admin-session";

interface ErrorResponse {
  message?: string;
}

export interface AdminPostInput {
  slug: string;
  title: string;
  excerpt?: string;
  contentMd: string;
  visibility: AdminPostVisibility;
  status: AdminPostStatus;
  lang: string;
  coverMediaAssetId?: number;
}

export interface AdminPostUpdateInput {
  slug: string;
  title: string;
  excerpt?: string;
  contentMd: string;
  visibility: AdminPostVisibility;
  lang: string;
  coverMediaAssetId?: number;
}

function toApiUrl(path: string) {
  return `${runtimeConfig.apiBaseUrl.replace(/\/$/, "")}${path}`;
}

async function requestAdminApi<T>(path: string, init: RequestInit): Promise<T> {
  const session = getValidAdminSession();

  if (!session) {
    throw new Error("관리자 인증이 필요합니다. 다시 로그인하세요.");
  }

  const response = await fetch(toApiUrl(path), {
    ...init,
    headers: {
      "Content-Type": "application/json",
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
      (data as ErrorResponse | null)?.message ?? "관리자 글 요청에 실패했습니다.",
    );
  }

  return data as T;
}

export function createAdminPost(input: AdminPostInput) {
  return requestAdminApi<AdminPostRecord>("/admin/posts", {
    method: "POST",
    body: JSON.stringify(input),
  });
}

export function updateAdminPost(postId: number, input: AdminPostUpdateInput) {
  return requestAdminApi<AdminPostRecord>(`/admin/posts/${postId}`, {
    method: "PUT",
    body: JSON.stringify(input),
  });
}

export function changeAdminPostStatus(postId: number, status: AdminPostStatus) {
  return requestAdminApi<AdminPostRecord>(`/admin/posts/${postId}/status`, {
    method: "PATCH",
    body: JSON.stringify({ status }),
  });
}
