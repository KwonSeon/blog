import type { Metadata } from "next";
import Link from "next/link";
import { POST_CATEGORY_OPTIONS } from "@/src/entities/post";
import { siteConfig } from "@/src/shared/config/site";
import { mockPosts } from "@/src/shared/lib/mock/home-data";
import { Container, SectionHeader, SurfaceCard } from "@/src/shared/ui";

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

  return (
    <>
      <section className="py-16 sm:py-20 lg:py-24" aria-labelledby="posts-page-heading">
        <Container>
          <div className="grid gap-8 lg:grid-cols-[minmax(0,1.2fr)_minmax(280px,0.8fr)] lg:items-start">
            <div className="max-w-4xl">
              <p className="text-xs uppercase tracking-[0.24em] text-primary">
                Posts
              </p>
              <h1
                id="posts-page-heading"
                className="mt-4 text-balance text-4xl font-semibold tracking-tight text-foreground sm:text-5xl"
              >
                검색 유입으로 들어온 글도, 카테고리별 아카이브도 같은 흐름으로 탐색합니다
              </h1>
              <p className="mt-5 max-w-2xl text-base leading-8 text-muted-foreground">
                튜토리얼, 시스템 메모, 개발 기록, 회고를 한 화면에서 탐색할 수 있게
                정리합니다. 이번 단계는 공개 글 목록의 검색/필터 UI 구조를 먼저 잡고,
                이후 상세 화면과 API 연동으로 확장할 수 있게 만드는 것이 목표입니다.
              </p>

              <form action="/posts" method="get" className="mt-8 grid gap-3 sm:grid-cols-[minmax(0,1fr)_auto]">
                <label className="sr-only" htmlFor="posts-search-input">
                  글 검색
                </label>
                <input
                  id="posts-search-input"
                  name="q"
                  defaultValue={q}
                  placeholder="제목이나 요약으로 글 찾기"
                  className="min-h-12 rounded-full border border-border bg-background px-5 text-sm text-foreground outline-none transition focus:border-primary focus:ring-2 focus:ring-ring"
                />
                <input type="hidden" name="lang" value={selectedLang} />
                <button
                  type="submit"
                  className="inline-flex min-h-12 items-center justify-center rounded-full bg-primary px-6 text-sm font-medium text-primary-foreground transition hover:bg-primary/90 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                >
                  검색
                </button>
              </form>

              <div className="mt-6 flex flex-wrap gap-2">
                <Link
                  href="/posts"
                  className={`inline-flex min-h-10 items-center rounded-full border px-4 text-sm transition ${
                    selectedCategory
                      ? "border-border bg-background text-muted-foreground hover:bg-secondary/60"
                      : "border-primary/20 bg-primary/10 text-primary"
                  }`}
                >
                  전체
                </Link>
                {POST_CATEGORY_OPTIONS.map((category) => (
                  <Link
                    key={category.value}
                    href={`/posts?category=${category.value}`}
                    className={`inline-flex min-h-10 items-center rounded-full border px-4 text-sm transition ${
                      selectedCategory === category.value
                        ? "border-primary/20 bg-primary/10 text-primary"
                        : "border-border bg-background text-muted-foreground hover:bg-secondary/60"
                    }`}
                  >
                    {category.label}
                  </Link>
                ))}
              </div>
            </div>

            <SurfaceCard padding="lg">
              <p className="text-xs uppercase tracking-[0.24em] text-muted-foreground">
                Archive Summary
              </p>
              <p className="mt-4 text-base leading-7 text-foreground">
                글 목록은 홈의 최신 글 섹션보다 탐색 밀도가 높아야 합니다. 검색어,
                카테고리, 언어 기준을 한 화면에서 확인하고 결과 grid로 자연스럽게
                내려가도록 조립합니다.
              </p>
              <div className="mt-8 grid gap-3 sm:grid-cols-3 lg:grid-cols-1">
                <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                  <p className="text-xs text-muted-foreground">공개 글 수</p>
                  <p className="mt-2 text-sm text-foreground">{mockPosts.length}개</p>
                </div>
                <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                  <p className="text-xs text-muted-foreground">현재 검색어</p>
                  <p className="mt-2 text-sm text-foreground">{q || "없음"}</p>
                </div>
                <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                  <p className="text-xs text-muted-foreground">현재 카테고리</p>
                  <p className="mt-2 text-sm text-foreground">
                    {selectedCategory || "전체"}
                  </p>
                </div>
              </div>
            </SurfaceCard>
          </div>
        </Container>
      </section>

      <section className="pb-16 sm:pb-20" aria-labelledby="posts-list-heading">
        <Container>
          <SectionHeader
            headingId="posts-list-heading"
            title="글 목록"
            description="검색어, 카테고리, 발행 흐름을 기준으로 결과를 비교할 수 있게 정리합니다."
          />
        </Container>
      </section>
    </>
  );
}
