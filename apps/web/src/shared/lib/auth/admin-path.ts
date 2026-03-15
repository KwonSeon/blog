export function resolveAdminNextPath(candidate?: string | null) {
  if (!candidate || !candidate.startsWith("/admin")) {
    return "/admin";
  }

  return candidate;
}

export function buildAdminLoginPath(nextPath: string) {
  return `/admin/login?next=${encodeURIComponent(resolveAdminNextPath(nextPath))}`;
}
