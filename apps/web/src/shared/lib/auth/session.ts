import "server-only";
import { cookies } from "next/headers";
import { ssoSettings, refreshSsoToken } from "./sso";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
export const cookieOptions = {
  httpOnly: true,
  secure: process.env.NODE_ENV === "production",
  sameSite: "lax" as const,
  path: "/",
};
export async function saveTokens(tokens: {
  access_token: string;
  refresh_token?: string;
  expires_in?: number;
}) {
  const jar = await cookies();
  jar.set("blog.access", tokens.access_token, {
    ...cookieOptions,
    maxAge: tokens.expires_in ?? 900,
  });
  if (tokens.refresh_token)
    jar.set("blog.refresh", tokens.refresh_token, {
      ...cookieOptions,
      maxAge: 604800,
    });
}
export async function clearTokens() {
  const jar = await cookies();
  for (const name of ["blog.access", "blog.refresh", "blog.admin.session"])
    jar.set(name, "", { ...cookieOptions, maxAge: 0 });
}
export async function activeToken(allowRefresh = false) {
  if (process.env.IDENTITY_SSO_ENABLED !== "true") return null;
  const jar = await cookies();
  let token = jar.get("blog.access")?.value;
  if (!token && allowRefresh && jar.get("blog.refresh")?.value) {
    try {
      const tokens = await refreshSsoToken(jar.get("blog.refresh")!.value);
      await saveTokens(tokens);
      token = tokens.access_token;
    } catch {
      return null;
    }
  }
  if (!token) return null;
  const response = await fetch(`${ssoSettings().issuer.origin}/api/auth/me`, {
    headers: { Authorization: `Bearer ${token}` },
    cache: "no-store",
    signal: AbortSignal.timeout(10000),
  });
  return response.ok ? token : null;
}
export async function editorToken(allowRefresh = false) {
  const token = await activeToken(allowRefresh);
  if (!token) return null;
  const response = await fetch(
    `${serverRuntimeConfig.blogApiBaseUrl}/admin/session`,
    {
      headers: { Authorization: `Bearer ${token}` },
      cache: "no-store",
      signal: AbortSignal.timeout(10000),
    },
  );
  return response.ok ? token : null;
}
export function sameOrigin(request: Request) {
  return request.headers.get("origin") === ssoSettings().redirectUri.origin;
}
