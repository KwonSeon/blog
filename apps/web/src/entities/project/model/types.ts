export type ProjectStatus = "active" | "experimental" | "archived";

export interface Project {
  id: string;
  slug: string;
  title: string;
  description: string;
  tags: string[];
  status: ProjectStatus;
  coverImage?: string;
  demoUrl?: string;
  repoUrl?: string;
  detailUrl: string;
  featured?: boolean;
}

export const PROJECT_STATUS_LABELS: Record<ProjectStatus, string> = {
  active: "운영 중",
  experimental: "실험 중",
  archived: "아카이브",
};

export const PROJECT_STATUS_VARIANTS = {
  active: "default",
  experimental: "secondary",
  archived: "outline",
} as const;
