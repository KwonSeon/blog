import type { Metadata } from "next";
import { notFound } from "next/navigation";
import {
  POST_CATEGORY_LABELS,
  PostTagList,
} from "@/src/entities/post";
import { siteConfig } from "@/src/shared/config/site";
import { getMockPostBySlug } from "@/src/shared/lib/mock/home-data";
import {
  CTAButton,
  Container,
  StatusBadge,
  SurfaceCard,
} from "@/src/shared/ui";

interface PostDetailPageProps {
  params: Promise<{
    slug: string;
  }>;
}

export async function generateMetadata({
  params,
}: PostDetailPageProps): Promise<Metadata> {
  const { slug } = await params;
  const post = getMockPostBySlug(slug);

  if (!post) {
    return {
      title: "글을 찾을 수 없음",
      robots: {
        index: false,
        follow: false,
      },
    };
  }

  return {
    title: post.title,
    description: post.excerpt,
    alternates: {
      canonical: `/posts/${post.slug}`,
    },
    openGraph: {
      title: `${post.title} | ${siteConfig.name}`,
      description: post.excerpt,
      url: `${siteConfig.url}/posts/${post.slug}`,
      siteName: siteConfig.name,
      type: "article",
    },
  };
}

export default async function PostDetailPage({ params }: PostDetailPageProps) {
  const { slug } = await params;
  const post = getMockPostBySlug(slug);

  if (!post) {
    notFound();
  }

  const formattedDate = new Date(post.publishedAt).toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });

  return (
    <section className="py-16 sm:py-20 lg:py-24" aria-labelledby="post-detail-heading">
      <Container>
        <div className="grid gap-8">
          <div className="grid gap-8 lg:grid-cols-[minmax(0,1.15fr)_minmax(280px,0.85fr)] lg:items-end">
            <div className="max-w-4xl">
              <div className="flex flex-wrap items-center gap-2">
                <StatusBadge variant="accent">
                  {POST_CATEGORY_LABELS[post.category]}
                </StatusBadge>
                <StatusBadge variant="outline">{post.lang.toUpperCase()}</StatusBadge>
                <span className="text-sm text-muted-foreground">slug: {post.slug}</span>
              </div>

              <p className="mt-5 text-xs uppercase tracking-[0.24em] text-primary">
                Post Detail
              </p>
              <h1
                id="post-detail-heading"
                className="mt-4 text-balance text-4xl font-semibold tracking-tight text-foreground sm:text-5xl"
              >
                {post.title}
              </h1>
              <p className="mt-6 max-w-2xl text-base leading-8 text-muted-foreground">
                {post.excerpt}
              </p>

              <div className="mt-8 flex flex-col gap-3 sm:flex-row">
                <CTAButton href="/posts" size="lg">
                  글 목록으로 돌아가기
                </CTAButton>
                <CTAButton href="/" variant="outline" size="lg">
                  홈으로 이동
                </CTAButton>
              </div>
            </div>

            <SurfaceCard padding="lg">
              <p className="text-xs uppercase tracking-[0.24em] text-muted-foreground">
                Summary
              </p>
              <div className="mt-4 grid gap-3 sm:grid-cols-2 lg:grid-cols-1">
                <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                  <p className="text-xs text-muted-foreground">발행일</p>
                  <p className="mt-2 text-sm text-foreground">{formattedDate}</p>
                </div>
                <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                  <p className="text-xs text-muted-foreground">읽기 시간</p>
                  <p className="mt-2 text-sm text-foreground">{post.readingTime}</p>
                </div>
              </div>

              <PostTagList tags={post.tags} className="mt-6" />
            </SurfaceCard>
          </div>

          <div className="grid gap-6 lg:grid-cols-[minmax(0,1.1fr)_minmax(280px,0.9fr)]">
            <SurfaceCard as="article" padding="lg">
              <p className="text-xs uppercase tracking-[0.24em] text-primary">
                Article Body
              </p>
              <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
                본문 레이아웃 베이스
              </h2>
              <div className="mt-5 space-y-4 text-base leading-8 text-muted-foreground">
                <p>
                  이 영역은 다음 단계에서 markdown renderer를 붙여 실제 글 본문을
                  article 계층 구조로 렌더링할 자리입니다.
                </p>
                <p>
                  현재 단계에서는 route, metadata, not-found, 상세 상단 정보와 본문
                  shell 구조를 먼저 고정합니다.
                </p>
              </div>
            </SurfaceCard>

            <div className="grid gap-6">
              <SurfaceCard padding="lg">
                <p className="text-xs uppercase tracking-[0.24em] text-primary">
                  Detail Flow
                </p>
                <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
                  상세 화면 흐름
                </h2>
                <div className="mt-5 space-y-3 text-sm leading-7 text-muted-foreground">
                  <p>상단 hero에서 글 메타와 요약을 먼저 보여줍니다.</p>
                  <p>중앙 article 영역에서 markdown 본문을 읽게 만듭니다.</p>
                  <p>다음 단계에서 관련 프로젝트 CTA와 하단 이동 흐름을 붙입니다.</p>
                </div>
              </SurfaceCard>
            </div>
          </div>
        </div>
      </Container>
    </section>
  );
}
