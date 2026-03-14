import { CTAButton, Container, SurfaceCard } from "@/src/shared/ui";

export function HomeCTA() {
  return (
    <section className="py-16 sm:py-20" aria-labelledby="home-cta-heading">
      <Container size="narrow">
        <SurfaceCard padding="lg" className="text-center">
          <p className="text-xs uppercase tracking-[0.24em] text-primary">
            Next step
          </p>
          <h2
            id="home-cta-heading"
            className="mt-4 text-2xl font-semibold tracking-tight text-foreground sm:text-3xl"
          >
            프로젝트를 먼저 둘러보거나, 글로 정리된 배경을 읽어보세요
          </h2>
          <p className="mt-4 text-base leading-8 text-muted-foreground">
            C, 시스템, 웹 개발 기록을 꾸준히 정리하고 직접 만든 서비스를 함께 공개합니다. 이후 이 위치에는 추천 콘텐츠나 구독 CTA도 자연스럽게 붙을 수 있습니다.
          </p>

          <div className="mt-8 flex flex-col items-center justify-center gap-3 sm:flex-row">
            <CTAButton href="/projects">프로젝트 둘러보기</CTAButton>
            <CTAButton href="/posts" variant="outline">
              글 읽으러 가기
            </CTAButton>
          </div>
        </SurfaceCard>
      </Container>
    </section>
  );
}
