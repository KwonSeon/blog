import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { PostCard } from "@/src/entities/post";
import {
  PROJECT_STATUS_LABELS,
  PROJECT_STATUS_VARIANTS,
} from "@/src/entities/project";
import { siteConfig } from "@/src/shared/config/site";
import {
  getMockPostsByProjectSlug,
  getMockProjectBySlug,
  mockPosts,
} from "@/src/shared/lib/mock/home-data";
import {
  CTAButton,
  Container,
  SectionHeader,
  StatusBadge,
  SurfaceCard,
} from "@/src/shared/ui";

interface ProjectDetailPageProps {
  params: Promise<{
    slug: string;
  }>;
}

export async function generateMetadata({
  params,
}: ProjectDetailPageProps): Promise<Metadata> {
  const { slug } = await params;
  const project = getMockProjectBySlug(slug);

  if (!project) {
    return {
      title: "프로젝트를 찾을 수 없음",
    };
  }

  return {
    title: project.title,
    description: project.description,
    alternates: {
      canonical: `/projects/${project.slug}`,
    },
    openGraph: {
      title: `${project.title} | ${siteConfig.name}`,
      description: project.description,
      url: `${siteConfig.url}/projects/${project.slug}`,
      siteName: siteConfig.name,
      type: "article",
    },
  };
}

export default async function ProjectDetailPage({
  params,
}: ProjectDetailPageProps) {
  const { slug } = await params;
  const project = getMockProjectBySlug(slug);

  if (!project) {
    notFound();
  }

  const relatedPosts = getMockPostsByProjectSlug(project.slug);
  const suggestedPosts =
    relatedPosts.length > 0
      ? relatedPosts.slice(0, 3)
      : mockPosts.filter((post) => post.slug !== project.slug).slice(0, 2);

  return (
    <>
      <Container as="section" className="py-16 sm:py-20 lg:py-24">
        <div className="grid gap-8">
          <div className="grid gap-8 lg:grid-cols-[minmax(0,1.15fr)_minmax(280px,0.85fr)] lg:items-end">
            <div className="max-w-4xl">
              <div className="flex flex-wrap items-center gap-2">
                <StatusBadge variant={PROJECT_STATUS_VARIANTS[project.status]}>
                  {PROJECT_STATUS_LABELS[project.status]}
                </StatusBadge>
                <span className="text-sm text-muted-foreground">
                  slug: {project.slug}
                </span>
              </div>

              <p className="mt-5 text-xs uppercase tracking-[0.24em] text-primary">
                Project Detail
              </p>
              <h1 className="mt-4 text-balance text-4xl font-semibold tracking-tight text-foreground sm:text-5xl">
                {project.title}
              </h1>
              <p className="mt-6 max-w-2xl text-base leading-8 text-muted-foreground">
                {project.description}
              </p>

              <div className="mt-8 flex flex-col gap-3 sm:flex-row">
                {project.demoUrl ? (
                  <CTAButton href={project.demoUrl} size="lg">
                    서비스 보기
                  </CTAButton>
                ) : null}
                <CTAButton href="/projects" variant="outline" size="lg">
                  목록으로 돌아가기
                </CTAButton>
              </div>
            </div>

            <SurfaceCard padding="lg">
              <p className="text-xs uppercase tracking-[0.24em] text-muted-foreground">
                Summary
              </p>
              <p className="mt-4 text-base leading-7 text-foreground">
                이 프로젝트는 홈 화면에서 요약 카드로 보여주던 항목을 상세 단위로
                확장하는 첫 공개 화면입니다. 서비스 진입 CTA와 기술 정보, 배경 설명을
                단계적으로 붙여갈 수 있는 구조로 조립합니다.
              </p>
              <div className="mt-8 grid gap-3 sm:grid-cols-2 lg:grid-cols-1">
                <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                  <p className="text-xs text-muted-foreground">현재 상태</p>
                  <p className="mt-2 text-sm text-foreground">
                    {PROJECT_STATUS_LABELS[project.status]}
                  </p>
                </div>
                <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                  <p className="text-xs text-muted-foreground">진입 흐름</p>
                  <p className="mt-2 text-sm text-foreground">
                    상세 확인 후 서비스 링크 또는 후속 관련 글 탐색
                  </p>
                </div>
              </div>
            </SurfaceCard>
          </div>

          <div className="grid gap-6 lg:grid-cols-[minmax(0,1.1fr)_minmax(280px,0.9fr)]">
            <SurfaceCard as="article" padding="lg">
              <p className="text-xs uppercase tracking-[0.24em] text-primary">
                Overview
              </p>
              <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
                프로젝트 개요
              </h2>
              <div className="mt-5 space-y-4 text-base leading-8 text-muted-foreground">
                <p>
                  {project.title}는 공개 홈에서 방문자가 바로 탐색할 수 있도록 정리한
                  대표 프로젝트 중 하나입니다.
                </p>
                <p>{project.description}</p>
                <p>
                  상세 화면에서는 이후 구현 배경, 문제 정의, 주요 화면 흐름, 관련 글
                  연결까지 자연스럽게 이어질 수 있도록 정보 블록을 분리합니다.
                </p>
              </div>
            </SurfaceCard>

            <div className="grid gap-6">
              <aside>
                <SurfaceCard padding="lg">
                  <p className="text-xs uppercase tracking-[0.24em] text-primary">
                    Service Links
                  </p>
                  <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
                    서비스 링크
                  </h2>
                  <div className="mt-5 flex flex-col gap-3">
                    {project.demoUrl ? (
                      <CTAButton href={project.demoUrl}>서비스 바로가기</CTAButton>
                    ) : null}
                    <CTAButton href="/projects" variant="outline">
                      프로젝트 목록
                    </CTAButton>
                  </div>
                </SurfaceCard>
              </aside>

              <aside>
                <SurfaceCard padding="lg">
                  <p className="text-xs uppercase tracking-[0.24em] text-primary">
                    Tech Info
                  </p>
                  <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
                    기술 정보
                  </h2>
                  <div className="mt-5 flex flex-wrap gap-2">
                    {project.tags.map((tag) => (
                      <span
                        key={tag}
                        className="rounded-full bg-secondary px-3 py-1 text-sm text-secondary-foreground"
                      >
                        {tag}
                      </span>
                    ))}
                  </div>
                </SurfaceCard>
              </aside>
            </div>
          </div>
        </div>
      </Container>

      <section
        className="border-t border-border bg-secondary/20 py-16 sm:py-20"
        aria-labelledby="project-related-posts-heading"
      >
        <Container>
          <SectionHeader
            headingId="project-related-posts-heading"
            title="관련 글"
            description={
              relatedPosts.length > 0
                ? "이 프로젝트와 연결된 설계 메모, 구현 기록, 운영 회고를 이어서 읽을 수 있게 정리합니다."
                : "직접 연결된 글은 아직 적지만, 현재 공개 중인 설계 기록과 개발 메모를 먼저 읽어볼 수 있습니다."
            }
            viewAllHref="/projects"
            viewAllLabel="프로젝트 목록으로"
          />

          <div className="grid gap-2 md:grid-cols-2 xl:grid-cols-3">
            {suggestedPosts.map((post, index) => (
              <PostCard
                key={post.id}
                post={post}
                className={index === 0 ? "md:col-span-2 xl:col-span-1" : undefined}
              />
            ))}
          </div>
        </Container>
      </section>

      <section className="py-16 sm:py-20" aria-labelledby="project-detail-cta-heading">
        <Container size="narrow">
          <SurfaceCard padding="lg" className="text-center">
            <p className="text-xs uppercase tracking-[0.24em] text-primary">
              Next step
            </p>
            <h2
              id="project-detail-cta-heading"
              className="mt-4 text-2xl font-semibold tracking-tight text-foreground sm:text-3xl"
            >
              다른 프로젝트를 더 둘러보거나, 지금 이 서비스를 바로 확인해보세요
            </h2>
            <p className="mt-4 text-base leading-8 text-muted-foreground">
              프로젝트 상세는 단일 소개 페이지로 끝나지 않고, 다른 공개 프로젝트 탐색과
              관련 글 읽기 흐름으로 이어져야 합니다. 이후 이 위치에는 배포 상태, 업데이트
              로그, 추천 프로젝트 CTA도 자연스럽게 붙일 수 있습니다.
            </p>

            <div className="mt-8 flex flex-col items-center justify-center gap-3 sm:flex-row">
              <CTAButton href="/projects">다른 프로젝트 보기</CTAButton>
              {project.demoUrl ? (
                <CTAButton href={project.demoUrl} variant="outline">
                  이 프로젝트 확인하기
                </CTAButton>
              ) : null}
            </div>
          </SurfaceCard>
        </Container>
      </section>
    </>
  );
}
