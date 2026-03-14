import type { Metadata } from "next";
import { notFound } from "next/navigation";
import {
  POST_CATEGORY_LABELS,
  PostDetailHero,
  PostMarkdown,
  PostRelatedProjectCta,
  PostTagList,
} from "@/src/entities/post";
import {
  getMockPostDetailBySlug,
  getMockProjectBySlug,
} from "@/src/shared/lib/mock/home-data";
import { buildPublicMetadata } from "@/src/shared/lib/seo/public-metadata";
import {
  CTAButton,
  Container,
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
  const post = getMockPostDetailBySlug(slug);

  if (!post) {
    return {
      title: "글을 찾을 수 없음",
      robots: {
        index: false,
        follow: false,
      },
    };
  }

  return buildPublicMetadata({
    title: post.title,
    description: post.excerpt,
    path: `/posts/${post.slug}`,
    type: "article",
    keywords: [
      post.title,
      POST_CATEGORY_LABELS[post.category],
      post.relatedProjectTitle ?? "",
      ...post.tags,
    ],
    publishedTime: `${post.publishedAt}T00:00:00+09:00`,
    section: POST_CATEGORY_LABELS[post.category],
    tags: post.tags,
  });
}

export default async function PostDetailPage({ params }: PostDetailPageProps) {
  const { slug } = await params;
  const post = getMockPostDetailBySlug(slug);

  if (!post) {
    notFound();
  }

  const relatedProject = post.relatedProjectSlug
    ? getMockProjectBySlug(post.relatedProjectSlug)
    : undefined;
  const formattedDate = new Date(post.publishedAt).toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });

  return (
    <>
      <section className="py-16 sm:py-20 lg:py-24" aria-labelledby="post-detail-heading">
        <Container>
          <div className="grid gap-8">
          <div className="grid gap-8 lg:grid-cols-[minmax(0,1.15fr)_minmax(280px,0.85fr)] lg:items-end">
              <PostDetailHero post={post} relatedProject={relatedProject} />

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
                <p className="text-xs uppercase tracking-[0.24em] text-primary">Article</p>
                <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
                  본문
                </h2>
                <PostMarkdown content={post.contentMd} className="mt-8" />
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
                    <p>관련 프로젝트와 다른 공개 화면으로 이동하는 CTA를 함께 둡니다.</p>
                  </div>
                </SurfaceCard>

                <PostRelatedProjectCta project={relatedProject} />
              </div>
            </div>
          </div>
        </Container>
      </section>

      <section className="py-16 sm:py-20" aria-labelledby="post-detail-cta-heading">
        <Container size="narrow">
          <SurfaceCard padding="lg" className="text-center">
            <p className="text-xs uppercase tracking-[0.24em] text-primary">Next step</p>
            <h2
              id="post-detail-cta-heading"
              className="mt-4 text-2xl font-semibold tracking-tight text-foreground sm:text-3xl"
            >
              이어서 다른 글을 읽거나, 연결된 프로젝트를 직접 확인해보세요
            </h2>
            <p className="mt-4 text-base leading-8 text-muted-foreground">
              공개 글 상세는 본문 독해로 끝나지 않고 다른 글 탐색과 프로젝트 체험
              흐름으로 이어져야 합니다. 이후 이 위치에는 추천 글, 발행 맥락, 추가 CTA를
              더 붙일 수 있습니다.
            </p>

            <div className="mt-8 flex flex-col items-center justify-center gap-3 sm:flex-row">
              <CTAButton href="/posts">다른 글 보기</CTAButton>
              {relatedProject ? (
                <CTAButton href={`/projects/${relatedProject.slug}`} variant="outline">
                  연결 프로젝트 이동
                </CTAButton>
              ) : (
                <CTAButton href="/" variant="outline">
                  홈으로 돌아가기
                </CTAButton>
              )}
            </div>
          </SurfaceCard>
        </Container>
      </section>
    </>
  );
}
