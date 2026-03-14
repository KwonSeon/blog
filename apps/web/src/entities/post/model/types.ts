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
  category: PostCategory;
  tags: string[];
  publishedAt: string;
  readingTime: string;
  relatedProjectSlug?: string;
  relatedProjectTitle?: string;
}

export const POST_CATEGORY_LABELS: Record<PostCategory, string> = {
  tutorial: "튜토리얼",
  devlog: "개발 기록",
  retrospective: "회고",
  system: "시스템",
  web: "웹 개발",
};
