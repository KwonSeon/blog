import Link from "next/link";
import { PostCard, type Post } from "@/src/entities/post";
import { Container, SectionHeader, SurfaceCard } from "@/src/shared/ui";

interface PostsResultsSectionProps {
  posts: Post[];
  q: string;
  selectedCategoryLabel: string;
  selectedLang: string;
}

export function PostsResultsSection({
  posts,
  q,
  selectedCategoryLabel,
  selectedLang,
}: PostsResultsSectionProps) {
  return (
    <section className="pb-16 sm:pb-20" aria-labelledby="posts-list-heading">
      <Container>
        <SectionHeader
          headingId="posts-list-heading"
          title="글 목록"
          description="검색어, 카테고리, 발행 흐름을 기준으로 결과를 비교할 수 있게 정리합니다."
        />

        <div className="mb-6 flex flex-wrap items-center justify-between gap-3 rounded-3xl border border-border bg-secondary/20 px-5 py-4 text-sm text-muted-foreground">
          <div className="flex flex-wrap items-center gap-x-4 gap-y-2">
            <span>결과 {posts.length}개</span>
            <span>검색어: {q || "없음"}</span>
            <span>카테고리: {selectedCategoryLabel}</span>
            <span>언어: {selectedLang}</span>
          </div>
          {q || selectedCategoryLabel !== "전체" ? (
            <Link
              href="/posts"
              className="font-medium text-primary transition-colors hover:text-primary/80"
            >
              필터 초기화
            </Link>
          ) : null}
        </div>

        {posts.length > 0 ? (
          <div className="grid gap-2 md:grid-cols-2 xl:grid-cols-3">
            {posts.map((post, index) => (
              <PostCard
                key={post.id}
                post={post}
                className={index === 0 ? "md:col-span-2 xl:col-span-1" : undefined}
              />
            ))}
          </div>
        ) : (
          <SurfaceCard padding="lg" className="text-center">
            <p className="text-xs uppercase tracking-[0.24em] text-primary">
              No Results
            </p>
            <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
              조건에 맞는 글이 아직 없습니다
            </h2>
            <p className="mt-4 text-base leading-8 text-muted-foreground">
              검색어를 줄이거나 카테고리를 전체로 되돌리면 더 많은 글을 볼 수 있습니다.
              공개 글 상세와 검색 API는 이후 단계에서 계속 확장할 예정입니다.
            </p>
            <div className="mt-8 flex items-center justify-center">
              <Link
                href="/posts"
                className="inline-flex min-h-11 items-center rounded-full bg-primary px-6 text-sm font-medium text-primary-foreground transition hover:bg-primary/90"
              >
                전체 글로 돌아가기
              </Link>
            </div>
          </SurfaceCard>
        )}
      </Container>
    </section>
  );
}
