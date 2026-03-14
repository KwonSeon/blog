export const runtimeConfig = {
  apiBaseUrl:
    process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api",
  mediaApiBaseUrl:
    process.env.NEXT_PUBLIC_MEDIA_API_BASE_URL ?? "http://localhost:8082/api",
} as const;
