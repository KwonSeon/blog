import { CTAButton, Container, SurfaceCard } from "@/src/shared/ui";

export default function NotFound() {
  return (
    <section className="py-20 sm:py-24 lg:py-28">
      <Container size="narrow">
        <SurfaceCard padding="lg" className="text-center">
          <p className="text-xs uppercase tracking-[0.24em] text-primary">Not Found</p>
          <h1 className="mt-4 text-3xl font-semibold tracking-tight text-foreground sm:text-4xl">
            요청한 공개 페이지를 찾을 수 없습니다
          </h1>
          <p className="mt-4 text-base leading-8 text-muted-foreground">
            주소가 바뀌었거나 아직 공개되지 않은 글/프로젝트일 수 있습니다. 홈에서 다시
            탐색하거나, 현재 공개 중인 목록으로 이동해 주세요.
          </p>

          <div className="mt-8 flex flex-col items-center justify-center gap-3 sm:flex-row">
            <CTAButton href="/">홈으로 돌아가기</CTAButton>
            <CTAButton href="/projects" variant="outline">
              프로젝트 보기
            </CTAButton>
            <CTAButton href="/posts" variant="outline">
              글 보기
            </CTAButton>
          </div>
        </SurfaceCard>
      </Container>
    </section>
  );
}
