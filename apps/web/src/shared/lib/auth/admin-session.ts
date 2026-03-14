export interface AdminSession {
  accessToken: string;
  expiresAt: string;
}

const ADMIN_ACCESS_TOKEN_KEY = "blog.admin.accessToken";
const ADMIN_EXPIRES_AT_KEY = "blog.admin.expiresAt";

export function saveAdminSession(session: AdminSession) {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(ADMIN_ACCESS_TOKEN_KEY, session.accessToken);
  window.localStorage.setItem(ADMIN_EXPIRES_AT_KEY, session.expiresAt);
}

export function readAdminSession(): AdminSession | null {
  if (typeof window === "undefined") {
    return null;
  }

  const accessToken = window.localStorage.getItem(ADMIN_ACCESS_TOKEN_KEY);
  const expiresAt = window.localStorage.getItem(ADMIN_EXPIRES_AT_KEY);

  if (!accessToken || !expiresAt) {
    return null;
  }

  return {
    accessToken,
    expiresAt,
  };
}

export function clearAdminSession() {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.removeItem(ADMIN_ACCESS_TOKEN_KEY);
  window.localStorage.removeItem(ADMIN_EXPIRES_AT_KEY);
}

export function isAdminSessionExpired(session: AdminSession) {
  return new Date(session.expiresAt).getTime() <= Date.now();
}

export function getValidAdminSession() {
  const session = readAdminSession();

  if (!session) {
    return null;
  }

  if (isAdminSessionExpired(session)) {
    clearAdminSession();
    return null;
  }

  return session;
}
