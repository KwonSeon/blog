import type {
  AdminPostRecord,
  AdminPostStatus,
  AdminPostVisibility,
} from "@/src/entities/post";

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
  coverMediaAssetId?: string | number;
}

export interface AdminPostUpdateInput {
  slug: string;
  title: string;
  excerpt?: string;
  contentMd: string;
  visibility: AdminPostVisibility;
  lang: string;
  coverMediaAssetId?: string | number;
}

async function requestAdminApi<T>(path: string, init: RequestInit): Promise<T> {
  const response = await fetch(`/admin/api${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init.headers ?? {}),
    },
    cache: "no-store",
  });

  const data = (await response.json().catch(() => null)) as
    T | ErrorResponse | null;

  if (response.status === 401 || response.status === 403) {
    throw new Error("관리자 인증이 만료됐습니다. 다시 로그인하세요.");
  }

  if (!response.ok) {
    throw new Error(
      (data as ErrorResponse | null)?.message ??
        "관리자 글 요청에 실패했습니다.",
    );
  }

  return data as T;
}

export function createAdminPost(input: AdminPostInput) {
  return requestAdminApi<AdminPostRecord>("/posts", {
    method: "POST",
    body: JSON.stringify(input),
  });
}

export function updateAdminPost(
  postId: string | number,
  input: AdminPostUpdateInput,
) {
  return requestAdminApi<AdminPostRecord>(`/posts/${postId}`, {
    method: "PUT",
    body: JSON.stringify(input),
  });
}

export function changeAdminPostStatus(
  postId: string | number,
  status: AdminPostStatus,
) {
  return requestAdminApi<AdminPostRecord>(`/posts/${postId}/status`, {
    method: "PATCH",
    body: JSON.stringify({ status }),
  });
}
