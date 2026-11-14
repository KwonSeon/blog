import { Container, SurfaceCard, CTAButton } from "@/src/shared/ui";
export function AdminLoginShell({ children }: { children: React.ReactNode }) {
  return (
    <main className="min-h-screen py-16">
      <Container size="narrow">
        <div className="mb-8">
          <CTAButton href="/" variant="secondary">
            블로그로 돌아가기
          </CTAButton>
        </div>
        <SurfaceCard padding="lg">{children}</SurfaceCard>
      </Container>
    </main>
  );
}
