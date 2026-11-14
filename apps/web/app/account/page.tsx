import { activeToken, editorToken } from "@/src/shared/lib/auth/session";
import { CTAButton, Container, SurfaceCard } from "@/src/shared/ui";
import { AdminLogoutButton } from "@/src/widgets/admin-session-guard/ui/admin-logout-button";
export const dynamic = "force-dynamic";
export default async function Account() {
  const token = await activeToken().catch(() => null),
    editor = token ? await editorToken().catch(() => null) : null;
  return (
    <Container size="narrow" className="py-16">
      <SurfaceCard>
        <h1 className="text-3xl font-semibold">내 계정</h1>
        <p className="my-6">
          {token
            ? "통합 계정으로 로그인했습니다."
            : "통합 계정으로 로그인해 주세요."}
        </p>
        <div className="flex flex-wrap gap-4">
          {token ? (
            <>
              <CTAButton href="/account/notifications">알림함</CTAButton>
              <AdminLogoutButton />
              {editor ? (
                <CTAButton href="/admin">글 관리</CTAButton>
              ) : (
                <p>글 작성 권한이 없는 계정입니다.</p>
              )}
            </>
          ) : (
            <>
              <CTAButton href="/api/auth/sso/start">로그인</CTAButton>
              <CTAButton href="/register" variant="secondary">
                회원가입
              </CTAButton>
            </>
          )}
          <CTAButton href="/" variant="secondary">
            블로그로 돌아가기
          </CTAButton>
        </div>
      </SurfaceCard>
    </Container>
  );
}
