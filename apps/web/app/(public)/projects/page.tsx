import { ProjectCard } from "@/src/entities/project";
import { getProjectsPageData } from "@/src/shared/lib/api/public-page-data";
import { Container, CTAButton } from "@/src/shared/ui";
export const metadata = {
  title: "프로젝트",
  description: "직접 만든 서비스와 공개 소스 코드 모음",
};
export default async function Projects() {
  const { projects, totalCount } = await getProjectsPageData();
  return (
    <Container className="py-16">
      <header className="mb-12">
        <p className="text-sm tracking-widest text-primary">PROJECTS</p>
        <h1 className="mt-4 text-4xl font-semibold">직접 만든 것들</h1>
        <p className="mt-5 text-lg leading-8 text-muted-foreground">
          아이디어에서 서비스까지. 프로젝트의 소개와 구현을 살펴보세요.
        </p>
        <p className="mt-4 text-sm text-muted-foreground">
          {totalCount}개의 공개 프로젝트
        </p>
      </header>
      <div className="grid gap-6 lg:grid-cols-2">
        {projects.map((p) => (
          <ProjectCard key={p.id} project={p} />
        ))}
        {!projects.length && (
          <p className="text-muted-foreground">
            프로젝트 소개를 준비하고 있습니다.
          </p>
        )}
      </div>
      <div className="mt-12">
        <CTAButton
          href="https://github.com/KwonSeon"
          external
          variant="secondary"
        >
          GitHub에서 더 보기
        </CTAButton>
      </div>
    </Container>
  );
}
