import type { Metadata } from "next";
import { ProjectCard } from "@/src/entities/project";
import { siteConfig } from "@/src/shared/config/site";
import { mockProjects } from "@/src/shared/lib/mock/home-data";
import { CTAButton, Container, SectionHeader, SurfaceCard } from "@/src/shared/ui";

export const metadata: Metadata = {
  title: "프로젝트",
  description:
    "직접 만들고 운영하거나 실험 중인 프로젝트를 둘러볼 수 있는 공개 프로젝트 목록 화면입니다.",
  alternates: {
    canonical: "/projects",
  },
  openGraph: {
    title: `프로젝트 | ${siteConfig.name}`,
    description:
      "직접 만들고 운영하거나 실험 중인 프로젝트를 둘러볼 수 있는 공개 프로젝트 목록 화면입니다.",
    url: `${siteConfig.url}/projects`,
    siteName: siteConfig.name,
    type: "website",
  },
};

export default function ProjectsPage() {
  return (
    <>
      <section className="py-16 sm:py-20 lg:py-24" aria-labelledby="projects-page-heading">
        <Container>
          <div className="grid gap-8 lg:grid-cols-[minmax(0,1.15fr)_minmax(280px,0.85fr)] lg:items-end">
            <div className="max-w-4xl">
              <p className="text-xs uppercase tracking-[0.24em] text-primary">
                Projects
              </p>
              <h1
                id="projects-page-heading"
                className="mt-4 text-balance text-4xl font-semibold tracking-tight text-foreground sm:text-5xl"
              >
                직접 만들고 운영하거나 실험 중인 프로젝트를 한곳에 모았습니다
              </h1>
              <p className="mt-5 max-w-2xl text-base leading-8 text-muted-foreground">
                홈에서 일부만 보여준 프로젝트를 서비스 랜딩 관점으로 다시 정리합니다.
                무엇을 만들었는지, 어떤 문제를 풀었는지, 지금 바로 어디까지 써볼 수
                있는지를 빠르게 파악할 수 있게 구성합니다.
              </p>

              <div className="mt-8 flex flex-col gap-3 sm:flex-row">
                <CTAButton href="#project-list" size="lg">
                  프로젝트 둘러보기
                </CTAButton>
                <CTAButton href="/" variant="outline" size="lg">
                  홈으로 돌아가기
                </CTAButton>
              </div>
            </div>

            <SurfaceCard padding="lg">
              <p className="text-xs uppercase tracking-[0.24em] text-muted-foreground">
                Explore
              </p>
              <p className="mt-4 text-base leading-7 text-foreground">
                운영 중인 서비스, 실험 프로젝트, 아카이브 항목을 같은 흐름 안에서
                보여주고 각 프로젝트의 상세 화면으로 자연스럽게 이어지게 만듭니다.
              </p>
              <div className="mt-8 grid gap-3 sm:grid-cols-3 lg:grid-cols-1">
                <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                  <p className="text-xs text-muted-foreground">목록 역할</p>
                  <p className="mt-2 text-sm text-foreground">프로젝트 탐색과 상태 비교</p>
                </div>
                <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                  <p className="text-xs text-muted-foreground">상세 역할</p>
                  <p className="mt-2 text-sm text-foreground">서비스 진입 CTA와 구현 배경</p>
                </div>
                <div className="rounded-2xl bg-secondary/70 px-4 py-4">
                  <p className="text-xs text-muted-foreground">구조 기준</p>
                  <p className="mt-2 text-sm text-foreground">링크 중심 공개 화면과 SEO</p>
                </div>
              </div>
            </SurfaceCard>
          </div>
        </Container>
      </section>

      <section id="project-list" className="pb-16 sm:pb-20" aria-labelledby="project-list-heading">
        <Container>
          <SectionHeader
            headingId="project-list-heading"
            title="프로젝트 목록"
            description="운영 상태, 기술 태그, 상세 링크, 서비스 진입 흐름을 같은 카드 언어로 비교할 수 있게 정리합니다."
          />

          <div className="grid gap-6 lg:grid-cols-2">
            {mockProjects.map((project, index) => (
              <ProjectCard
                key={project.id}
                project={project}
                featured={index === 0}
                className={index === 0 ? "lg:col-span-2" : undefined}
              />
            ))}
          </div>
        </Container>
      </section>
    </>
  );
}
