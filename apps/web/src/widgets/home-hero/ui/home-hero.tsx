import { CTAButton, Container, StatusBadge, SurfaceCard } from "@/src/shared/ui";

interface HomeHeroProps {
  headline: string;
  subheadline: string;
  description: string;
  currentFocus: string[];
  stats: {
    projects: number;
    posts: number;
    yearsOfDev: number;
  };
}

export function HomeHero({
  headline,
  subheadline,
  description,
  currentFocus,
  stats,
}: HomeHeroProps) {
  return (
    <section className="relative overflow-hidden border-b border-border py-16 sm:py-20 lg:py-24">
      <div className="pointer-events-none absolute inset-0">
        <div className="absolute left-[8%] top-0 h-64 w-64 rounded-full bg-primary/8 blur-3xl" />
        <div className="absolute bottom-0 right-[10%] h-72 w-72 rounded-full bg-accent/10 blur-3xl" />
      </div>

      <Container className="relative">
        <div className="grid gap-8 lg:grid-cols-[minmax(0,1.2fr)_minmax(280px,0.8fr)] lg:items-end">
          <div>
            <div className="mb-5 flex flex-wrap items-center gap-2">
              <StatusBadge variant="success">프로젝트 랜딩 + 블로그 허브</StatusBadge>
              <span className="text-sm text-muted-foreground">
                개발 기록과 직접 만든 서비스를 함께 공개합니다
              </span>
            </div>

            <p className="text-sm font-medium uppercase tracking-[0.24em] text-primary">
              {subheadline}
            </p>
            <h1 className="mt-4 max-w-4xl text-balance text-4xl font-semibold tracking-tight text-foreground sm:text-5xl lg:text-6xl">
              {headline}
            </h1>
            <p className="mt-6 max-w-2xl text-pretty text-base leading-8 text-muted-foreground sm:text-lg">
              {description}
            </p>

            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <CTAButton href="/projects" size="lg">
                프로젝트 보기
              </CTAButton>
              <CTAButton href="/posts" variant="outline" size="lg">
                글 보러가기
              </CTAButton>
            </div>

            <div className="mt-8 flex flex-wrap gap-2">
              {currentFocus.map((focus) => (
                <span
                  key={focus}
                  className="rounded-full border border-border bg-background/80 px-3 py-1 text-sm text-muted-foreground"
                >
                  최근 집중 분야: {focus}
                </span>
              ))}
            </div>
          </div>

          <SurfaceCard
            padding="lg"
            className="bg-[linear-gradient(180deg,rgba(255,255,255,0.48),rgba(255,255,255,0.9))] backdrop-blur"
          >
            <p className="text-xs uppercase tracking-[0.24em] text-muted-foreground">
              Snapshot
            </p>
            <p className="mt-4 text-base leading-7 text-foreground">
              프로젝트를 먼저 탐색하고, 이어서 글 아카이브에서 설계 배경과 구현 기록을 읽는 흐름을 기본 구조로 잡았습니다.
            </p>

            <dl className="mt-8 grid grid-cols-3 gap-3">
              <div className="rounded-2xl bg-secondary/70 px-3 py-4 text-center">
                <dt className="text-xs text-muted-foreground">프로젝트</dt>
                <dd className="mt-1 text-2xl font-semibold text-foreground">
                  {stats.projects}
                </dd>
              </div>
              <div className="rounded-2xl bg-secondary/70 px-3 py-4 text-center">
                <dt className="text-xs text-muted-foreground">글</dt>
                <dd className="mt-1 text-2xl font-semibold text-foreground">
                  {stats.posts}+
                </dd>
              </div>
              <div className="rounded-2xl bg-secondary/70 px-3 py-4 text-center">
                <dt className="text-xs text-muted-foreground">개발</dt>
                <dd className="mt-1 text-2xl font-semibold text-foreground">
                  {stats.yearsOfDev}년
                </dd>
              </div>
            </dl>

            <div className="mt-8 border-t border-border pt-6">
              <p className="text-sm leading-7 text-muted-foreground">
                검색 유입 이후에도 바로 프로젝트를 살펴볼 수 있게, 텍스트와 링크 중심의 공개 구조를 유지합니다.
              </p>
            </div>
          </SurfaceCard>
        </div>
      </Container>
    </section>
  );
}
