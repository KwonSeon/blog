import "server-only";

function normalizeBaseUrl(value: string) {
  return value.replace(/\/$/, "");
}

export const serverRuntimeConfig = {
  blogApiBaseUrl: normalizeBaseUrl(
    process.env.BLOG_API_BASE_URL ??
      process.env.NEXT_PUBLIC_API_BASE_URL ??
      "http://localhost:8080/api",
  ),
  mediaApiBaseUrl: normalizeBaseUrl(
    process.env.MEDIA_API_BASE_URL ??
      process.env.NEXT_PUBLIC_MEDIA_API_BASE_URL ??
      "http://localhost:8082/api",
  ),
} as const;
