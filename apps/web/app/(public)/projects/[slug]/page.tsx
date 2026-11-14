import { notFound } from "next/navigation";
import { getProjectDetailPageData } from "@/src/shared/lib/api/public-page-data";
import { ProjectDetailHero } from "@/src/entities/project";
import { PostCard } from "@/src/entities/post";
import { Container, SurfaceCard, CTAButton } from "@/src/shared/ui";
export async function generateMetadata({
  params,
}: {
  params: Promise<{ slug: string }>;
}) {
  const { slug } = await params;
  const data = await getProjectDetailPageData(slug);
  return {
    title: data?.project.title ?? "프로젝트",
    description: data?.project.description,
  };
}
export default async function Detail({
  params,
}: {
  params: Promise<{ slug: string }>;
}) {
  const { slug } = await params;
  const data = await getProjectDetailPageData(slug);
  if (!data) notFound();
  const { project, relatedPosts, hasDirectlyMatchedPosts } = data;
  return (
    <Container className="py-16">
      <ProjectDetailHero project={project} />
      <div className="mt-12 grid gap-6 md:grid-cols-2">
        <SurfaceCard padding="lg">
          <h2 className="text-2xl font-semibold">프로젝트 소개</h2>
          <p className="mt-5 whitespace-pre-wrap leading-8 text-muted-foreground">
            {project.description || "프로젝트 소개를 준비하고 있습니다."}
          </p>
        </SurfaceCard>
        <SurfaceCard padding="lg">
          <h2 className="text-2xl font-semibold">프로젝트 링크</h2>
          <p className="my-5 leading-7 text-muted-foreground">
            서비스를 사용해 보거나 저장소에서 구현과 문서를 살펴보세요.
          </p>
          <div className="flex flex-wrap gap-3">
            {project.demoUrl && (
              <CTAButton href={project.demoUrl} external>
                서비스 방문
              </CTAButton>
            )}
            {project.repoUrl && (
              <CTAButton href={project.repoUrl} external variant="secondary">
                Git 저장소 보기
              </CTAButton>
            )}
            {!project.demoUrl && !project.repoUrl && (
              <p>등록된 링크가 없습니다.</p>
            )}
          </div>
        </SurfaceCard>
      </div>
      <section className="mt-16">
        <h2 className="text-2xl font-semibold mb-6">
          {hasDirectlyMatchedPosts ? "관련 글" : "최근 기록"}
        </h2>
        <div className="grid gap-6 md:grid-cols-3">
          {relatedPosts.map((p) => (
            <PostCard key={p.id} post={p} />
          ))}
        </div>
      </section>
    </Container>
  );
}
