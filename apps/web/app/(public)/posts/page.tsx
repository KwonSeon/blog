import type { Metadata } from "next";
import { getPostsPageData } from "@/src/shared/lib/api/public-page-data";
import { buildPublicMetadata } from "@/src/shared/lib/seo/public-metadata";
import { PostsArchiveHero } from "@/src/widgets/posts-archive-hero";
import { PostsResultsSection } from "@/src/widgets/posts-results";

interface PostsPageProps {
  searchParams: Promise<{
    q?: string;
    lang?: string;
  }>;
}

export async function generateMetadata({
  searchParams,
}: PostsPageProps): Promise<Metadata> {
  const params = await searchParams;
  const q = params.q?.trim() ?? "";
  const lang = params.lang?.trim() ?? "";
  const hasFilters = Boolean(q || lang);
  const description = hasFilters
    ? `${q ? `"${q}" 검색어와 ` : ""}${lang ? `${lang} 언어 조건을 포함해 ` : ""}공개 글 결과를 탐색하는 화면입니다.`
    : "튜토리얼, 개발 기록, 시스템 메모, 회고를 검색과 필터 흐름으로 탐색할 수 있는 공개 글 목록 화면입니다.";

  return buildPublicMetadata({
    title: hasFilters ? "글 검색 결과" : "글",
    description,
    path: "/posts",
    keywords: ["글 목록", "개발 블로그", "기술 아카이브", lang, q],
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
  const selectedLang = params.lang?.trim() ?? "";
  const { posts, totalCount } = await getPostsPageData({
    q,
    lang: selectedLang || undefined,
  });

  return (
    <>
      <PostsArchiveHero
        q={q}
        selectedLang={selectedLang}
        totalCount={totalCount}
      />
      <PostsResultsSection
        posts={posts}
        q={q}
        selectedLang={selectedLang}
      />
    </>
  );
}
