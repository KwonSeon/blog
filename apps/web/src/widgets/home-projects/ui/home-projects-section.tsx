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
          description="직접 만든 서비스와 공개 소스 코드를 만나보세요."
          viewAllHref="/projects"
          viewAllLabel="전체 프로젝트 보기"
        />

        {!projects.length && <p className="py-8 text-muted-foreground">프로젝트 소개를 준비하고 있습니다.</p>}
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
