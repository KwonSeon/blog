import "server-only";
import { cache } from "react";
import type { SiteSettings } from "@/src/shared/lib/site-settings";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
export const getSiteSettings = cache(async (): Promise<SiteSettings> => {
  const response = await fetch(
    `${serverRuntimeConfig.blogApiBaseUrl}/public/site-settings`,
    { cache: "no-store", signal: AbortSignal.timeout(10000) },
  );
  if (!response.ok) throw new Error("블로그 설정을 불러오지 못했습니다.");
  return response.json();
});
