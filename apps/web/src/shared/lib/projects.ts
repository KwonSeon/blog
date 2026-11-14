export interface ManagedProject {
  projectId: string;
  slug: string;
  title: string;
  summary?: string;
  serviceUrl?: string;
  repoUrl?: string;
  visibility: "PUBLIC" | "PRIVATE";
  status: "ACTIVE" | "INACTIVE";
}
export function safeProjectUrl(value: string | null | undefined) {
  if (!value) return undefined;
  try {
    const url = new URL(value);
    return url.protocol === "https:" && !url.username && !url.password
      ? url.href
      : undefined;
  } catch {
    return undefined;
  }
}
