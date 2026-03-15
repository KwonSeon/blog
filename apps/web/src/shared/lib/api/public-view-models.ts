import type {
  Post,
  PostDetail,
} from "@/src/entities/post";
import type {
  Project,
  ProjectStatus,
} from "@/src/entities/project";
import type {
  PublicPostDetail,
  PublicPostListItem,
  PublicProjectDetail,
  PublicProjectListItem,
} from "@/src/shared/lib/api/public-api";

const DEFAULT_POST_CATEGORY = "general" as const;

function formatReadingTime(sourceText: string) {
  const normalizedText = sourceText.replace(/\s+/g, "");
  const estimatedMinutes = Math.max(1, Math.ceil(normalizedText.length / 500));

  return `${estimatedMinutes}분`;
}

function resolveProjectStatus(input: {
  serviceUrl?: string | null;
  repoUrl?: string | null;
}): ProjectStatus {
  if (input.serviceUrl) {
    return "active";
  }

  if (input.repoUrl) {
    return "experimental";
  }

  return "archived";
}

function buildProjectTags(input: {
  serviceUrl?: string | null;
  repoUrl?: string | null;
  coverMediaAssetId?: number | null;
}) {
  const tags = ["공개 프로젝트"];

  if (input.serviceUrl) {
    tags.push("서비스 링크");
  }

  if (input.repoUrl) {
    tags.push("저장소");
  }

  if (input.coverMediaAssetId) {
    tags.push("커버 이미지");
  }

  return tags;
}

function buildPostTags(input: {
  lang: string;
  coverMediaAssetId?: number | null;
  contentMd?: string;
}) {
  const tags = [input.lang.toUpperCase(), "공개 글"];

  if (input.coverMediaAssetId) {
    tags.push("커버 이미지");
  }

  if (input.contentMd && input.contentMd.length >= 1800) {
    tags.push("장문");
  }

  return tags;
}

function resolvePublishedAt(input: {
  publishedAt?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}) {
  return input.publishedAt ?? input.createdAt ?? input.updatedAt ?? new Date().toISOString();
}

export function toProjectListViewModel(project: PublicProjectListItem): Project {
  return {
    id: String(project.projectId),
    slug: project.slug,
    title: project.title,
    description: project.summary,
    tags: buildProjectTags(project),
    status: resolveProjectStatus(project),
    demoUrl: project.serviceUrl ?? undefined,
    detailUrl: `/projects/${project.slug}`,
  };
}

export function toProjectDetailViewModel(project: PublicProjectDetail): Project {
  return {
    id: String(project.projectId),
    slug: project.slug,
    title: project.title,
    description: project.summary,
    tags: buildProjectTags(project),
    status: resolveProjectStatus(project),
    demoUrl: project.serviceUrl ?? undefined,
    detailUrl: `/projects/${project.slug}`,
  };
}

export function toPostListViewModel(post: PublicPostListItem): Post {
  const publishedAt = resolvePublishedAt(post);

  return {
    id: String(post.postId),
    slug: post.slug,
    title: post.title,
    excerpt: post.excerpt,
    lang: post.lang,
    category: DEFAULT_POST_CATEGORY,
    tags: buildPostTags(post),
    publishedAt,
    readingTime: formatReadingTime(`${post.title} ${post.excerpt}`),
  };
}

export function toPostDetailViewModel(post: PublicPostDetail): PostDetail {
  const publishedAt = resolvePublishedAt(post);

  return {
    id: String(post.postId),
    slug: post.slug,
    title: post.title,
    excerpt: post.excerpt,
    lang: post.lang,
    category: DEFAULT_POST_CATEGORY,
    tags: buildPostTags(post),
    publishedAt,
    readingTime: formatReadingTime(`${post.title} ${post.excerpt} ${post.contentMd}`),
    contentMd: post.contentMd,
  };
}
