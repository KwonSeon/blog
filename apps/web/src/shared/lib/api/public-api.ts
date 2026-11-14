import "server-only";
import { cache } from "react";
import { parseApiJson } from "./json";

import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";

export interface PublicProjectListItem {
  projectId: string | number;
  slug: string;
  title: string;
  summary: string;
  serviceUrl: string | null;
  coverMediaAssetId: string | number | null;
  publishedAt: string | null;
}

export interface PublicProjectDetail {
  projectId: string | number;
  slug: string;
  title: string;
  summary: string;
  serviceUrl: string | null;
  repoUrl: string | null;
  coverMediaAssetId: string | number | null;
  publishedAt: string | null;
}

export interface PublicPostListItem {
  postId: string | number;
  slug: string;
  title: string;
  excerpt: string;
  lang: string;
  coverMediaAssetId: string | number | null;
  publishedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PublicPostDetail {
  postId: string | number;
  slug: string;
  title: string;
  excerpt: string;
  contentMd: string;
  lang: string;
  coverMediaAssetId: string | number | null;
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

// Request-scoped memoization shares parsed data between metadata and the page.
// No persistent cache: a newly private/unpublished item disappears on the next request.
const requestPublicApiJson = cache(async (path: string, query = ""): Promise<unknown | null> => {
  const response = await fetch(`${serverRuntimeConfig.blogApiBaseUrl}${path}${query ? `?${query}` : ""}`, {
    cache: "no-store",
    signal: AbortSignal.timeout(8000),
    headers: { Accept: "application/json" },
  });
  if (response.status === 404) return null;
  if (!response.ok) throw new Error(`공개 API 요청에 실패했습니다. (${response.status})`);
  return parseApiJson(await response.text());
});

export async function listPublicProjects() {
  const result = await requestPublicApiJson("/projects");
  if (!result) throw new Error("프로젝트 목록을 불러오지 못했습니다.");
  return result as PublicProjectListResponse;
}

export async function getPublicProject(slug: string) {
  return await requestPublicApiJson(`/projects/${encodeURIComponent(slug)}`) as PublicProjectDetail | null;
}

export async function listPublicPosts(params?: { q?: string; lang?: string }) {
  const query = new URLSearchParams();
  if (params?.q) query.set("q", params.q);
  if (params?.lang) query.set("lang", params.lang);
  const result = await requestPublicApiJson("/posts", query.toString());
  if (!result) throw new Error("글 목록을 불러오지 못했습니다.");
  return result as PublicPostListResponse;
}

export async function getPublicPost(slug: string) {
  return await requestPublicApiJson(`/posts/${encodeURIComponent(slug)}`) as PublicPostDetail | null;
}
