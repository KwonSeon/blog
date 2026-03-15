import { Container, SurfaceCard } from "@/src/shared/ui";

export default function PublicLoading() {
  return (
    <section className="py-16 sm:py-20 lg:py-24">
      <Container>
        <div className="grid gap-6 lg:grid-cols-[minmax(0,1.2fr)_minmax(280px,0.8fr)]">
          <SurfaceCard padding="lg" className="min-h-72 animate-pulse bg-secondary/30">
            <div className="h-full min-h-60 rounded-2xl bg-background/20" />
          </SurfaceCard>
          <SurfaceCard padding="lg" className="min-h-72 animate-pulse bg-secondary/30">
            <div className="h-full min-h-60 rounded-2xl bg-background/20" />
          </SurfaceCard>
        </div>
      </Container>
    </section>
  );
}
