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
