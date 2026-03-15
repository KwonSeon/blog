import "server-only";

import { cookies } from "next/headers";

export const ADMIN_SESSION_COOKIE_NAME = "blog.admin.session";

const isProduction = process.env.NODE_ENV === "production";

function createCookieOptions(expiresAt?: string) {
  return {
    httpOnly: true,
    sameSite: "lax" as const,
    secure: isProduction,
    path: "/",
    ...(expiresAt ? { expires: new Date(expiresAt) } : {}),
  };
}

export async function setAdminSessionCookie(
  accessToken: string,
  expiresAt: string,
) {
  const cookieStore = await cookies();

  cookieStore.set(
    ADMIN_SESSION_COOKIE_NAME,
    accessToken,
    createCookieOptions(expiresAt),
  );
}

export async function clearAdminSessionCookie() {
  const cookieStore = await cookies();

  cookieStore.set(ADMIN_SESSION_COOKIE_NAME, "", {
    ...createCookieOptions(),
    maxAge: 0,
  });
}

export async function readAdminSessionToken() {
  const cookieStore = await cookies();
  return cookieStore.get(ADMIN_SESSION_COOKIE_NAME)?.value ?? null;
}
