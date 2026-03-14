import type { Metadata } from "next";
import {
  POST_CATEGORY_LABELS,
} from "@/src/entities/post";
import { filterMockPosts, mockPosts } from "@/src/shared/lib/mock/home-data";
import { buildPublicMetadata } from "@/src/shared/lib/seo/public-metadata";
import { PostsArchiveHero } from "@/src/widgets/posts-archive-hero";
import { PostsResultsSection } from "@/src/widgets/posts-results";

interface PostsPageProps {
  searchParams: Promise<{
    q?: string;
    category?: string;
    lang?: string;
  }>;
}

export async function generateMetadata({
  searchParams,
}: PostsPageProps): Promise<Metadata> {
  const params = await searchParams;
  const q = params.q?.trim() ?? "";
  const category = params.category?.trim() ?? "";
  const lang = params.lang?.trim() ?? "";
  const hasFilters = Boolean(q || category || lang);
  const categoryLabel = category
    ? POST_CATEGORY_LABELS[category as keyof typeof POST_CATEGORY_LABELS] ?? category
    : "";
  const description = hasFilters
    ? `${categoryLabel || "전체"} 범위에서 ${q ? `"${q}" 검색어와 ` : ""}${lang ? `${lang} 언어 조건을 포함해 ` : ""}공개 글 결과를 탐색하는 화면입니다.`
    : "튜토리얼, 개발 기록, 시스템 메모, 회고를 검색과 필터 흐름으로 탐색할 수 있는 공개 글 목록 화면입니다.";

  return buildPublicMetadata({
    title: hasFilters ? "글 검색 결과" : "글",
    description,
    path: "/posts",
    keywords: ["글 목록", "개발 블로그", "기술 아카이브", categoryLabel, lang, q],
    robots: hasFilters
      ? {
          index: false,
          follow: true,
        }
      : undefined,
  });
}

export default async function PostsPage({ searchParams }: PostsPageProps) {
  const params = await searchParams;
  const q = params.q?.trim() ?? "";
  const selectedCategory = params.category?.trim() ?? "";
  const selectedLang = params.lang?.trim() ?? "ko";
  const filteredPosts = filterMockPosts({
    q,
    category: selectedCategory || undefined,
    lang: selectedLang || undefined,
  });
  const selectedCategoryLabel = selectedCategory
    ? POST_CATEGORY_LABELS[selectedCategory as keyof typeof POST_CATEGORY_LABELS] ?? selectedCategory
    : "전체";

  return (
    <>
      <PostsArchiveHero
        q={q}
        selectedCategory={selectedCategory}
        selectedCategoryLabel={selectedCategoryLabel}
        selectedLang={selectedLang}
        totalCount={mockPosts.length}
      />
      <PostsResultsSection
        posts={filteredPosts}
        q={q}
        selectedCategoryLabel={selectedCategoryLabel}
        selectedLang={selectedLang}
      />
    </>
  );
}
