import type { Metadata } from "next";
import { siteConfig } from "@/src/shared/config/site";
import { Container, SurfaceCard } from "@/src/shared/ui";

export const metadata: Metadata = {
  title: "홈",
  description: siteConfig.description,
};

export default function HomePage() {
  return (
    <Container className="py-16 sm:py-20">
      <SurfaceCard padding="lg" className="max-w-3xl">
        <p className="text-xs uppercase tracking-[0.24em] text-primary">
          P0-015-FE-PUB-1
        </p>
        <h1 className="mt-4 text-3xl font-semibold tracking-tight text-foreground sm:text-4xl">
          공개 홈 컴포넌트 보일러플레이트를 먼저 이식했습니다
        </h1>
        <p className="mt-4 text-sm leading-7 text-muted-foreground sm:text-base">
          `frontexample` 기준의 공통 UI, entity 카드, mock data, public
          layout만 가져온 상태입니다. 실제 홈 화면 조립과 시안 구현은 다음 단계에서
          진행합니다.
        </p>
      </SurfaceCard>
    </Container>
  );
}
