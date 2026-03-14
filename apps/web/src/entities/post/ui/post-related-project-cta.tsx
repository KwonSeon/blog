import type { Project } from "@/src/entities/project";
import { CTAButton, SurfaceCard } from "@/src/shared/ui";

interface PostRelatedProjectCtaProps {
  project?: Project;
}

export function PostRelatedProjectCta({ project }: PostRelatedProjectCtaProps) {
  return (
    <SurfaceCard padding="lg">
      <p className="text-xs uppercase tracking-[0.24em] text-primary">
        Related Project
      </p>
      <h2 className="mt-4 text-2xl font-semibold tracking-tight text-foreground">
        연결 프로젝트
      </h2>
      {project ? (
        <>
          <p className="mt-5 text-base leading-8 text-muted-foreground">
            이 글은 <span className="font-medium text-foreground">{project.title}</span>
            의 설계 배경과 구현 기준을 설명하는 기록입니다. 글을 읽은 뒤 실제
            프로젝트 화면으로 이어서 이동할 수 있게 연결합니다.
          </p>
          <div className="mt-6 flex flex-col gap-3">
            <CTAButton href={`/projects/${project.slug}`}>
              프로젝트 상세 보기
            </CTAButton>
            {project.demoUrl ? (
              <CTAButton
                href={project.demoUrl}
                variant="outline"
                external={project.demoUrl.startsWith("http")}
              >
                서비스 바로가기
              </CTAButton>
            ) : null}
          </div>
        </>
      ) : (
        <p className="mt-5 text-base leading-8 text-muted-foreground">
          아직 직접 연결된 프로젝트는 없지만, 이 글과 같은 흐름의 작업은 프로젝트
          목록에서 계속 확장할 예정입니다.
        </p>
      )}
    </SurfaceCard>
  );
}
