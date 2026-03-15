import "server-only";

import { runtimeConfig } from "@/src/shared/config/runtime";

export interface PublicProjectListItem {
  projectId: number;
  slug: string;
  title: string;
  summary: string;
  serviceUrl: string | null;
  coverMediaAssetId: number | null;
  publishedAt: string | null;
}

export interface PublicProjectDetail {
  projectId: number;
  slug: string;
  title: string;
  summary: string;
  serviceUrl: string | null;
  repoUrl: string | null;
  coverMediaAssetId: number | null;
  publishedAt: string | null;
}

export interface PublicPostListItem {
  postId: number;
  slug: string;
  title: string;
  excerpt: string;
  lang: string;
  coverMediaAssetId: number | null;
  publishedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PublicPostDetail {
  postId: number;
  slug: string;
  title: string;
  excerpt: string;
  contentMd: string;
  lang: string;
  coverMediaAssetId: number | null;
  publishedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

interface PublicProjectListResponse {
  content: PublicProjectListItem[];
  totalCount: number;
}

interface PublicPostListResponse {
  content: PublicPostListItem[];
  totalCount: number;
}

interface RequestPublicApiJsonOptions {
  searchParams?: URLSearchParams;
  cache?: RequestCache;
  next?: NextFetchRequestConfig;
}

function toBackendApiUrl(path: string, searchParams?: URLSearchParams) {
  const normalizedBaseUrl = runtimeConfig.apiBaseUrl.replace(/\/$/, "");
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  const queryString = searchParams?.toString();

  return queryString
    ? `${normalizedBaseUrl}${normalizedPath}?${queryString}`
    : `${normalizedBaseUrl}${normalizedPath}`;
}

async function requestPublicApiJson<T>(
  path: string,
  options: RequestPublicApiJsonOptions = {},
): Promise<T> {
  const response = await fetch(toBackendApiUrl(path, options.searchParams), {
    cache: options.cache ?? "no-store",
    next: options.next,
    headers: {
      Accept: "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(`공개 API 요청에 실패했습니다. (${response.status})`);
  }

  return response.json() as Promise<T>;
}

export async function listPublicProjects() {
  const response = await requestPublicApiJson<PublicProjectListResponse>("/projects");

  return response;
}

export async function getPublicProject(slug: string) {
  const response = await fetch(toBackendApiUrl(`/projects/${slug}`), {
    cache: "no-store",
    headers: {
      Accept: "application/json",
    },
  });

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw new Error(`공개 프로젝트 상세 요청에 실패했습니다. (${response.status})`);
  }

  return response.json() as Promise<PublicProjectDetail>;
}

export async function listPublicPosts(params?: {
  q?: string;
  lang?: string;
}) {
  const searchParams = new URLSearchParams();

  if (params?.q) {
    searchParams.set("q", params.q);
  }

  if (params?.lang) {
    searchParams.set("lang", params.lang);
  }

  const response = await requestPublicApiJson<PublicPostListResponse>("/posts", {
    searchParams,
  });

  return response;
}

export async function getPublicPost(slug: string) {
  const response = await fetch(toBackendApiUrl(`/posts/${slug}`), {
    cache: "no-store",
    headers: {
      Accept: "application/json",
    },
  });

  if (response.status === 404) {
    return null;
  }

  if (!response.ok) {
    throw new Error(`공개 글 상세 요청에 실패했습니다. (${response.status})`);
  }

  return response.json() as Promise<PublicPostDetail>;
}
