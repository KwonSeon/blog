import type { Project } from "@/src/entities/project/model/types";
import {
  PROJECT_STATUS_LABELS,
  PROJECT_STATUS_VARIANTS,
} from "@/src/entities/project/model/types";
import { CTAButton, StatusBadge } from "@/src/shared/ui";

interface ProjectDetailHeroProps {
  project: Project;
}

export function ProjectDetailHero({ project }: ProjectDetailHeroProps) {
  return (
    <div className="max-w-4xl">
      <div className="flex flex-wrap items-center gap-2">
        <StatusBadge variant={PROJECT_STATUS_VARIANTS[project.status]}>
          {PROJECT_STATUS_LABELS[project.status]}
        </StatusBadge>
        <span className="text-sm text-muted-foreground">slug: {project.slug}</span>
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
  );
}
