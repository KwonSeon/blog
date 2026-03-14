import { ProjectCard, type Project } from "@/src/entities/project";
import { Container, SectionHeader } from "@/src/shared/ui";

interface HomeProjectsSectionProps {
  projects: Project[];
}

export function HomeProjectsSection({
  projects,
}: HomeProjectsSectionProps) {
  const [featuredProject, ...restProjects] = projects;

  return (
    <section className="py-16 sm:py-20" aria-labelledby="home-projects-heading">
      <Container>
        <SectionHeader
          headingId="home-projects-heading"
          title="대표 프로젝트"
          description="직접 만들고 운영하거나 실험 중인 프로젝트를 먼저 보여줍니다. 각 카드에서 자세한 설명과 서비스 진입 경로를 바로 제공합니다."
          viewAllHref="/projects"
          viewAllLabel="전체 프로젝트 보기"
        />

        <div className="grid gap-6 lg:grid-cols-2">
          {featuredProject ? (
            <ProjectCard
              project={featuredProject}
              featured
              className="lg:col-span-2"
            />
          ) : null}
          {restProjects.map((project) => (
            <ProjectCard key={project.id} project={project} />
          ))}
        </div>
      </Container>
    </section>
  );
}
