import Link from "next/link";
import { cn } from "@/lib/utils";
import {
  PROJECT_STATUS_LABELS,
  PROJECT_STATUS_VARIANTS,
  type Project,
} from "@/src/entities/project/model/types";
import { ProjectTagList } from "@/src/entities/project/ui/project-tag-list";
import { StatusBadge, SurfaceCard } from "@/src/shared/ui";

interface ProjectCardProps {
  project: Project;
  featured?: boolean;
  className?: string;
}

export function ProjectCard({
  project,
  featured = false,
  className,
}: ProjectCardProps) {
  return (
    <SurfaceCard
      as="article"
      interactive
      className={cn(
        "group flex h-full flex-col overflow-hidden",
        featured && "border-primary/20 bg-card lg:flex-row",
        className,
      )}
    >
      <div
        className={cn(
          "relative min-h-44 overflow-hidden border-b border-border bg-[radial-gradient(circle_at_top_left,rgba(52,98,73,0.22),transparent_45%),linear-gradient(135deg,rgba(130,112,53,0.12),rgba(255,255,255,0.4))]",
          featured && "lg:min-h-full lg:w-[42%] lg:border-b-0 lg:border-r",
        )}
      >
        <div className="absolute inset-0 bg-[linear-gradient(180deg,transparent,rgba(17,17,17,0.08))]" />
        <div className="relative flex h-full flex-col justify-between p-6">
          <div className="flex items-start justify-between gap-3">
            <StatusBadge variant={PROJECT_STATUS_VARIANTS[project.status]}>
              {PROJECT_STATUS_LABELS[project.status]}
            </StatusBadge>
            <span className="text-xs uppercase tracking-[0.2em] text-muted-foreground">
              {project.slug}
            </span>
          </div>

          <div>
            <p className="text-xs uppercase tracking-[0.24em] text-muted-foreground">
              Showcase
            </p>
            <p className="mt-2 text-3xl font-semibold tracking-tight text-foreground/80">
              {project.title.slice(0, 1)}
            </p>
          </div>
        </div>
      </div>

      <div className="flex flex-1 flex-col gap-4 p-6">
        <div className="space-y-3">
          <h3 className="text-xl font-semibold tracking-tight text-foreground">
            <Link
              href={project.detailUrl}
              className="transition-colors hover:text-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
            >
              {project.title}
            </Link>
          </h3>
          <p className="text-sm leading-7 text-muted-foreground">
            {project.description}
          </p>
        </div>

        <ProjectTagList tags={project.tags} />

        <div className="mt-auto flex flex-wrap items-center gap-4 text-sm">
          <Link
            href={project.detailUrl}
            className="inline-flex items-center gap-1 font-medium text-primary transition-colors hover:text-primary/80 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          >
            자세히 보기
            <span aria-hidden="true">→</span>
          </Link>
          {project.demoUrl ? (
            <Link
              href={project.demoUrl}
              className="inline-flex items-center gap-1 text-muted-foreground transition-colors hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
            >
              서비스 보기
              <span aria-hidden="true">↗</span>
            </Link>
          ) : null}
        </div>
      </div>
    </SurfaceCard>
  );
}
