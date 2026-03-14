import type { Metadata } from "next";
import {
  POST_CATEGORY_LABELS,
} from "@/src/entities/post";
import { siteConfig } from "@/src/shared/config/site";
import { filterMockPosts, mockPosts } from "@/src/shared/lib/mock/home-data";
import { PostsArchiveHero } from "@/src/widgets/posts-archive-hero";
import { PostsResultsSection } from "@/src/widgets/posts-results";

interface PostsPageProps {
  searchParams: Promise<{
    q?: string;
    category?: string;
    lang?: string;
  }>;
}

export const metadata: Metadata = {
  title: "글",
  description:
    "튜토리얼, 개발 기록, 시스템 메모, 회고를 검색과 필터 흐름으로 탐색할 수 있는 공개 글 목록 화면입니다.",
  alternates: {
    canonical: "/posts",
  },
  openGraph: {
    title: `글 | ${siteConfig.name}`,
    description:
      "튜토리얼, 개발 기록, 시스템 메모, 회고를 검색과 필터 흐름으로 탐색할 수 있는 공개 글 목록 화면입니다.",
    url: `${siteConfig.url}/posts`,
    siteName: siteConfig.name,
    type: "website",
  },
};

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
