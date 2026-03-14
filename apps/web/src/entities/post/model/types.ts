export type PostCategory =
  | "tutorial"
  | "devlog"
  | "retrospective"
  | "system"
  | "web";

export interface Post {
  id: string;
  slug: string;
  title: string;
  excerpt: string;
  lang: string;
  category: PostCategory;
  tags: string[];
  publishedAt: string;
  readingTime: string;
  relatedProjectSlug?: string;
  relatedProjectTitle?: string;
}

export interface PostDetail extends Post {
  contentMd: string;
}

export type AdminPostVisibility = "PUBLIC" | "PRIVATE";
export type AdminPostStatus = "DRAFT" | "PUBLISHED";

export interface AdminPostRecord {
  postId: number;
  slug: string;
  title: string;
  excerpt: string | null;
  contentMd: string;
  visibility: AdminPostVisibility;
  status: AdminPostStatus;
  lang: string;
  coverMediaAssetId: number | null;
  authorUserId: number;
  publishedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export const ADMIN_POST_VISIBILITY_OPTIONS: Array<{
  value: AdminPostVisibility;
  label: string;
}> = [
  { value: "PUBLIC", label: "공개" },
  { value: "PRIVATE", label: "비공개" },
];

export const ADMIN_POST_STATUS_OPTIONS: Array<{
  value: AdminPostStatus;
  label: string;
}> = [
  { value: "DRAFT", label: "임시저장" },
  { value: "PUBLISHED", label: "발행" },
];

export const POST_CATEGORY_LABELS: Record<PostCategory, string> = {
  tutorial: "튜토리얼",
  devlog: "개발 기록",
  retrospective: "회고",
  system: "시스템",
  web: "웹 개발",
};

export const POST_CATEGORY_OPTIONS: Array<{
  value: PostCategory;
  label: string;
}> = [
  { value: "tutorial", label: POST_CATEGORY_LABELS.tutorial },
  { value: "devlog", label: POST_CATEGORY_LABELS.devlog },
  { value: "retrospective", label: POST_CATEGORY_LABELS.retrospective },
  { value: "system", label: POST_CATEGORY_LABELS.system },
  { value: "web", label: POST_CATEGORY_LABELS.web },
];
