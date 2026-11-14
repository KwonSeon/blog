import { ActionButton } from "@/src/shared/ui/action-button";
import Link from "next/link";
import { Container, SurfaceCard } from "@/src/shared/ui";

interface PostsArchiveHeroProps {
  q: string;
  selectedLang: string;
  totalCount: number;
}

export function PostsArchiveHero({
  q,
  selectedLang,
  totalCount,
}: PostsArchiveHeroProps) {
  const buildPostsHref = (nextParams: { q?: string; lang?: string }) => {
    const query = new URLSearchParams();

    if (nextParams.q) {
      query.set("q", nextParams.q);
    }
    if (nextParams.lang) {
      query.set("lang", nextParams.lang);
    }

    const queryString = query.toString();
    return queryString ? `/posts?${queryString}` : "/posts";
  };

  return (
    <section
      className="py-16 sm:py-20 lg:py-24"
      aria-labelledby="posts-page-heading"
    >
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
              개발하며 배우고 기록한 이야기
            </h1>
            <p className="mt-5 max-w-2xl text-base leading-8 text-muted-foreground">
              설계와 구현, 문제 해결의 과정을 모았습니다. 관심 있는 주제를
              검색해 보세요.
            </p>

            <form
              action="/posts"
              method="get"
              className="mt-8 grid gap-3 sm:grid-cols-[minmax(0,1fr)_auto]"
            >
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
              <ActionButton
                type="submit"
                className="inline-flex min-h-12 items-center justify-center rounded-full bg-primary px-6 text-sm font-medium text-primary-foreground transition hover:bg-primary/90 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                검색
              </ActionButton>
            </form>

            <div className="mt-6 flex flex-wrap gap-2">
              <Link
                href={buildPostsHref({ q })}
                className={`inline-flex min-h-10 items-center rounded-full border px-4 text-sm transition ${
                  !selectedLang
                    ? "border-primary/20 bg-primary/10 text-primary"
                    : "border-border bg-background text-muted-foreground hover:bg-secondary/60"
                }`}
              >
                전체 언어
              </Link>
              <Link
                href={buildPostsHref({
                  q,
                  lang: "ko",
                })}
                className={`inline-flex min-h-10 items-center rounded-full border px-4 text-sm transition ${
                  selectedLang === "ko"
                    ? "border-primary/20 bg-primary/10 text-primary"
                    : "border-border bg-background text-muted-foreground hover:bg-secondary/60"
                }`}
              >
                한국어
              </Link>
            </div>
          </div>

          <SurfaceCard padding="lg">
            <p className="text-xs uppercase tracking-[0.24em] text-muted-foreground">
              Archive Summary
            </p>
            <p className="mt-4 text-base leading-7 text-foreground">
              검색어와 언어를 선택해 원하는 기록을 찾아보세요.
            </p>
            <div className="mt-8 grid gap-3 sm:grid-cols-3 lg:grid-cols-1">
              <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                <p className="text-xs text-muted-foreground">공개 글 수</p>
                <p className="mt-2 text-sm text-foreground">{totalCount}개</p>
              </div>
              <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                <p className="text-xs text-muted-foreground">현재 검색어</p>
                <p className="mt-2 text-sm text-foreground">{q || "없음"}</p>
              </div>
              <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                <p className="text-xs text-muted-foreground">현재 언어</p>
                <p className="mt-2 text-sm text-foreground">
                  {selectedLang ? selectedLang.toUpperCase() : "전체"}
                </p>
              </div>
            </div>
          </SurfaceCard>
        </div>
      </Container>
    </section>
  );
}
